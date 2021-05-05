package tokyo.aieuo.mineflow.utils

import cn.nukkit.utils.Config
import tokyo.aieuo.mineflow.formAPI.Form
import tokyo.aieuo.mineflow.trigger.TriggerHolder
import tokyo.aieuo.mineflow.trigger.form.FormTrigger

class FormManager(val config: Config) {

    val all: MutableMap<String, Form> = mutableMapOf()

    init {
        @Suppress("UNCHECKED_CAST")
        for ((name, _data) in config.all) {
            val data = _data as Map<String, *>
            all[name] = Form.createFromArray(data["form"] as Map<String, Any>, name) ?: continue
        }
    }

    fun saveAll() {
        config.save()
    }

    fun existsForm(name: String): Boolean {
        return all.containsKey(name)
    }

    fun addForm(name: String, form: Form) {
        val data = mapOf(
            "name" to name,
            "type" to form.type,
            "form" to jsonSerializableToMap(form.jsonSerialize()),
        )
        all[name] = form
        config.set(name, data)
        config.save()
    }

    fun getForm(name: String): Form? {
        return all[name]
    }

    fun removeForm(name: String) {
        all.remove(name)
        config.remove(name)
    }

    fun getNotDuplicatedName(name: String): String {
        if (!existsForm(name)) return name
        var count = 2
        while (existsForm("$name ($count)")) {
            count++
        }
        return "$name ($count)"
    }

    fun getAssignedRecipes(formName: String): Map<String, List<String>> {
        val recipes = mutableMapOf<String, MutableList<String>>()
        val containers = TriggerHolder.getRecipesWithSubKey(FormTrigger.create(formName))
        for ((name, container) in containers) {
            for (recipe in container.getAllRecipe().values) {
                val path = "${recipe.group}/${recipe.name}"
                if (!recipes.containsKey(path)) recipes[path] = mutableListOf()
                recipes[path]?.add(name)
            }
        }
        return recipes
    }
}