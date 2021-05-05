package tokyo.aieuo.mineflow.recipe

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.ObjectMapper
import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.command.CustomCommandData
import tokyo.aieuo.mineflow.flowItem.action.script.CreateConfigVariable
import tokyo.aieuo.mineflow.formAPI.Form
import tokyo.aieuo.mineflow.trigger.Triggers
import tokyo.aieuo.mineflow.utils.ConfigHolder
import tokyo.aieuo.mineflow.utils.JsonSerializable
import tokyo.aieuo.mineflow.utils.json_encode
import java.io.File
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Path

class RecipePack(
    val name: String,
    val author: String,
    val detail: String,
    val recipes: MutableList<Recipe>,
    commands: MutableMap<String, CustomCommandData>? = null,
    forms: MutableMap<String, Form>? = null,
    configs: MutableMap<String, Map<String, *>>? = null,
    val version: String = Main.pluginVersion
): JsonSerializable {

    val commands: MutableMap<String, CustomCommandData> = commands ?: getLinkedCommands()
    val forms: MutableMap<String, Form> = forms ?: getLinkedForms()
    val configs: MutableMap<String, Map<String, *>> = configs ?: getLinkedConfigFiles()

    private fun getLinkedCommands(): MutableMap<String, CustomCommandData> {
        val commandManager = Main.commandManager
        val commands = mutableMapOf<String, CustomCommandData>()
        for (recipe in recipes) {
            for (trigger in recipe.triggers) {
                if (trigger.type != Triggers.COMMAND) continue

                val key = trigger.key
                commands[key] = commandManager.getCommand(key) ?: continue
            }
        }
        return commands
    }

    private fun getLinkedForms(): MutableMap<String, Form> {
        val formManager = Main.formManager
        val forms = mutableMapOf<String, Form>()
        for (recipe in recipes) {
            for (trigger in recipe.triggers) {
                if (trigger.type != Triggers.FORM) continue

                val key = trigger.key
                forms[key] = formManager.getForm(key) ?: continue
            }
        }
        return forms
    }

    private fun getLinkedConfigFiles(): MutableMap<String, Map<String, *>> {
        val configData = mutableMapOf<String, Map<String, *>>()
        for (recipe in recipes) {
            for (action in recipe.getActions()) {
                if (action is CreateConfigVariable) {
                    val name = action.fileName
                    configData[name] = ConfigHolder.getConfig(name).all
                }
            }
        }
        return configData
    }

    fun export(path: String) {
        if (!Files.exists(Path.of(path))) File(path).mkdirs()

        val fw = FileWriter(File("$path$name.json"))
        fw.write(json_encode(this))
        fw.close()
    }

    override fun jsonSerialize(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "author" to author,
            "detail" to detail,
            "plugin_version" to version,
            "recipes" to recipes,
            "commands" to commands.mapValues { (_, v) -> v.toMap() },
            "forms" to forms,
            "configs" to configs,
        )
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun import(path: String): RecipePack? {
            if (!Files.exists(Path.of(path))) return null

            val packData = try {
                ObjectMapper().readValue(File(path).readText(), mutableMapOf<String, Any>().javaClass)
            } catch (e: JsonParseException) {
                return null
            }

            val name = packData["name"] as? String ?: ""
            val author = packData["author"] as? String ?: ""
            val detail = packData["detail"] as? String ?: ""

            val recipes = mutableListOf<Recipe>()
            for (data in packData["recipes"] as List<Map<String, Any>>) {
                val recipeName = data["name"] as? String ?: continue
                val recipeGroup = data["group"] as? String ?: ""
                val recipeAuthor = data["author"] as? String ?: ""

                val recipe = Recipe(recipeName, recipeGroup, recipeAuthor, data["plugin_version"] as? String)
                recipe.loadSaveData(data["actions"] as List<Map<String, Any>>)

                val target = data["target"] as? Map<String, Any> ?: mapOf()
                recipe.setTargetSetting(
                    target["type"] as? Int ?: Recipe.TARGET_DEFAULT,
                    target["options"] as? Map<String, Any> ?: mapOf()
                )
                recipe.setTriggersFromArray(data["triggers"] as? List<Map<String, String>> ?: listOf())
                recipe.arguments = data["arguments"] as? List<String> ?: listOf()
                recipe.returnValues = data["returnValues"] as? List<String> ?: listOf()
                recipe.checkVersion()

                recipes.add(recipe)
            }

            val commands = (packData["commands"] as? Map<String, Map<String, Any>>)?.mapValues { (_, v) ->
                CustomCommandData(
                    v["command"] as String,
                    v["permission"] as String,
                    v["description"] as String,
                    v["subCommands"] as? Map<String, Any> ?: mapOf()
                )
            }?.toMutableMap() ?: mutableMapOf()
            val forms = HashMap<String, Form>()
            for ((formName, formData) in packData["forms"] as? Map<String, Map<String, Any>> ?: mapOf()) {
                forms[formName] = Form.createFromArray(formData) ?: continue
            }
            val configs = packData["configs"] as? MutableMap<String, Map<String, *>> ?: mutableMapOf()

            val version = packData["plugin_version"] as? String ?: "0.0.0"

            return RecipePack(name, author, detail, recipes, commands, forms, configs, version)
        }
    }
}