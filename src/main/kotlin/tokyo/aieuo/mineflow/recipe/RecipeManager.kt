package tokyo.aieuo.mineflow.recipe

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.ObjectMapper
import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.exception.FlowItemLoadException
import tokyo.aieuo.mineflow.flowItem.FlowItemContainer
import tokyo.aieuo.mineflow.flowItem.action.script.ExecuteRecipe
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.utils.Logger
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class RecipeManager(val saveDir: String) {

    val all: MutableMap<String, MutableMap<String, Recipe>> = mutableMapOf()

    init {
        if (!Files.exists(Path.of(saveDir))) File(saveDir).mkdirs()
    }

    private fun getRecipeFiles(path: File): List<File> {
        val files = path.listFiles() ?: return listOf()
        val result = mutableListOf<File>()

        for (file in files) {
            if (file.isDirectory) {
                result.addAll(getRecipeFiles(file))
            } else if(file.extension == "json") {
                result.add(file)
            }
        }

        return result
    }

    @Suppress("UNCHECKED_CAST")
    fun loadRecipes() {
        val files = getRecipeFiles(File(saveDir))

        for (file in files) {
            val pathname = file.path
            val group = file.path
                .replace("\\", "/")
                .replace(saveDir.substring(0, saveDir.length - 1).replace("\\", "/"), "")
                .let { it.substring(0, it.lastIndexOf("/")) }
                .let { if (it.isNotEmpty()) it.substring(1) else it }

            val json = file.readText()
            val data = try {
                ObjectMapper().readValue(json, mutableMapOf<String, Any>().javaClass)
            } catch (e: JsonParseException) {
                Logger.warning(Language.get("recipe.json.decode.failed", listOf(pathname, e.message ?: "")))
                continue
            }

            if (!data.contains("name") || !data.contains("actions")) {
                Logger.warning(Language.get("recipe.json.decode.failed", listOf(pathname, Language.get("recipe.json.key.missing"))))
                continue
            }

            val name = data["name"] as String
            val author = data["author"] as? String ?: ""

            val recipe = Recipe(name, group, author, data["plugin_version"] as? String)
            recipe.rawData = json

            try {
                recipe.loadSaveData(data["actions"] as List<Map<String, Any>>)

                val target = data["target"] as? Map<String, Any> ?: mapOf()
                recipe.setTargetSetting(
                    target["type"] as? Int ?: Recipe.TARGET_DEFAULT,
                    target["options"] as? Map<String, Any> ?: mapOf()
                )
                recipe.setTriggersFromArray(data["triggers"] as? List<Map<String, String>> ?: listOf())
                recipe.arguments = data["arguments"] as? List<String> ?: listOf()
                recipe.returnValues = data["returnValues"] as? List<String> ?: listOf()
            } catch (e: Exception) {
                when (e) {
                    is FlowItemLoadException -> {
                        Logger.warning(Language.get("recipe.load.failed", listOf(name, "")))
                        e.message?.let { Logger.warning(it) }
                    }
                    else -> e.message?.let {
                        Logger.warning(Language.get("recipe.load.failed", listOf(name, it)))
                    }
                }
                continue
            }
            recipe.checkVersion()

            add(recipe, false)
        }
    }

    fun exists(name: String, group: String = ""): Boolean {
        return get(name, group) != null
    }

    fun add(recipe: Recipe, createFile: Boolean = true) {
        all.getOrPut(recipe.group, { mutableMapOf() })[recipe.name] = recipe
        if (createFile && !Files.exists(Path.of(recipe.getFileName(saveDir)))) {
            recipe.save(saveDir)
        }
    }

    fun get(name: String, group: String = ""): Recipe? {
        return all[group]?.get(name)
    }

    fun getByPath(name: String): Map<String, Map<String, Recipe>> {
        if (name.isEmpty()) return all

        val result = mutableMapOf<String, MutableMap<String, Recipe>>()
        for ((group, item) in all) {
            if ("${group}/".startsWith("${name}/")) result[group] = item
        }
        return result
    }

    fun remove(name: String, group: String = "") {
        if (!exists(name, group)) return

        Files.deleteIfExists(Paths.get(get(name, group)?.getFileName(saveDir) ?: return))
        all[group]?.remove(name)
    }

    fun deleteGroup(group: String): Boolean {
        return try {
            File(saveDir + group).delete()
            all.remove(group)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun saveAll() {
        for ((_, group) in all) {
            for ((_, recipe) in group) {
                recipe.save(saveDir)
            }
        }
    }

    fun getNotDuplicatedName(name: String, group: String = ""): String {
        if (!exists(name, group)) return name
        var count = 2
        while (exists("$name ($count)", group)) {
            count++
        }
        return "$name ($count)"
    }

    fun rename(recipeName: String, newName: String, group: String = "") {
        val recipe = get(recipeName, group) ?: return
        val oldPath = recipe.getFileName(saveDir)

        recipe.name = newName
        all[group]?.remove(recipeName)
        all.getOrPut(group, { mutableMapOf() })[newName] = recipe
        rename(oldPath, recipe.getFileName(saveDir))
    }

    fun parseName(name: String): Pair<String, String> {
        val names = ArrayDeque(name.split("/"))
        return (names.removeLastOrNull() ?: name) to names.joinToString("/")
    }

    fun getParentPath(group: String): String {
        val names = ArrayDeque(group.split("/"))
        names.removeLastOrNull()
        return names.joinToString("/")
    }

    fun getWithLinkedRecipes(container: FlowItemContainer, origin: Recipe, base: Boolean = true): Map<String, Recipe> {
        val recipes = mutableMapOf<String, Recipe>()
        if (base) recipes["${origin.group}/${origin.name}"] = origin
        for (action in container.getActions()) {
            if (action is FlowItemContainer) {
                val links = getWithLinkedRecipes(action, origin, false)
                recipes.putAll(links)
                continue
            }

            if (action is ExecuteRecipe) {
                val name = Main.variableHelper.replaceVariables(action.recipeName)

                var (recipeName, group) = Main.recipeManager.parseName(name)
                if (group.isEmpty()) group = origin.group

                var recipe = Main.recipeManager.get(recipeName, group)
                if (recipe === null) recipe = Main.recipeManager.get(recipeName, "")
                if (recipe === null) continue

                recipes.putAll(getWithLinkedRecipes(recipe, recipe))
            }
        }
        return recipes
    }
}