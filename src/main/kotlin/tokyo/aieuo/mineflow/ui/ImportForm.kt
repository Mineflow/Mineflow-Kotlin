package tokyo.aieuo.mineflow.ui

import cn.nukkit.Player
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.g00fy2.versioncompare.Version
import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.command.CustomCommandData
import tokyo.aieuo.mineflow.formAPI.Form
import tokyo.aieuo.mineflow.formAPI.ListForm
import tokyo.aieuo.mineflow.formAPI.ModalForm
import tokyo.aieuo.mineflow.formAPI.element.Button
import tokyo.aieuo.mineflow.recipe.Recipe
import tokyo.aieuo.mineflow.recipe.RecipePack
import tokyo.aieuo.mineflow.utils.ConfigHolder
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.utils.SimpleCallable
import java.io.File
import java.util.*
import kotlin.collections.LinkedHashMap

object ImportForm {

    const val FILE_EXT = "json"

    private fun getImportFiles(path: File, extension: String = "json"): List<File> {
        val files = path.listFiles() ?: return listOf()
        val result = mutableListOf<File>()

        for (file in files) {
            if(file.isFile && file.extension == extension) {
                result.add(file)
            }
        }

        return result
    }

    fun sendSelectImportFile(player: Player, messages: List<String> = listOf()) {
        val files = getImportFiles(File("${Main.instance.dataFolder.path}/imports/", FILE_EXT))

        val buttons = mutableListOf(
            Button("@form.back") { RecipeForm.sendMenu(player) }
        )
        for (file in files) {
            buttons.add(Button(file.name) {
                sendFileMenu(player, file)
            })
        }

        (ListForm("@form.import.selectFile.title"))
            .setButtons(buttons)
            .addMessages(messages)
            .show(player)
    }

    fun sendFileMenu(player: Player, file: File) {
        val data = try {
            ObjectMapper().readValue(file.readText(), mutableMapOf<String, Any>().javaClass)
        } catch (e: JsonParseException) {
            sendSelectImportFile(player, listOf(Language.get("recipe.json.decode.failed", listOf(file.name, e.message ?: ""))))
            return
        }

        (ListForm(file.name))
            .setContent("name: ${data["name"]}\ndetail: ${data["detail"]}\nauthor: ${data["author"]}")
            .setButtons(mutableListOf(
                Button("@form.back") { sendSelectImportFile(player) },
                Button("@form.import.selectFile") {
                    val pack = RecipePack.import(file.path) ?: return@Button
                    if (Version(Main.instance.description.version) < Version(pack.version)) {
                        player.sendMessage(Language.get("import.plugin.outdated"))
                        return@Button
                    }
                    importPack(player, pack)
                },
            )).show(player)

    }

    fun importPack(player: Player, pack: RecipePack) {
        importRecipes(player, pack.recipes) {
            importCommands(player, pack.commands.values.toList()) {
                importForms(player, pack.forms.values.toList()) {
                    importConfigs(player, LinkedHashMap(pack.configs), {
                        player.sendMessage(Language.get("form.import.success"))
                    })
                }
            }
        }
    }

    fun importRecipes(player: Player, recipes: MutableList<Recipe>, onComplete: SimpleCallable? = null, start: Int = 0) {
        val manager = Main.recipeManager
        for (i in start until recipes.size) {
            val recipe = recipes[i]

            if (manager.exists(recipe.name, recipe.group)) {
                confirmOverwrite(player, "${recipe.group}/${recipe.name}") {
                    if (it) {
                        manager.add(recipe)
                    }
                    importRecipes(player, recipes, onComplete, i + 1)
                }
                return
            }

            manager.add(recipe)
        }
        if (onComplete !== null) onComplete()
    }

    fun importRecipes(player: Player, recipes: MutableList<Recipe>, onComplete: SimpleCallable? = null) {
        importRecipes(player, recipes, onComplete, 0)
    }

    fun importCommands(player: Player, commands: List<CustomCommandData>, onComplete: SimpleCallable? = null, start: Int = 0) {
        val manager = Main.commandManager
        for (i in start until commands.size) {
            val data = commands[i]
            val command = data.command

            if (manager.existsCommand(command) || manager.isRegistered(command)) {
                confirmOverwrite(player, command) {
                    if (it) {
                        manager.addCommand(data.command, data.permission, data.description)
                    }
                    importCommands(player, commands, onComplete, i + 1)
                }
                return
            }

            manager.addCommand(data.command, data.permission, data.description)
        }
        if (onComplete !== null) onComplete()
    }

    fun importCommands(player: Player, commands: List<CustomCommandData>, onComplete: SimpleCallable? = null) {
        importCommands(player, commands, onComplete, 0)
    }

    fun importForms(player: Player, forms: List<Form>, onComplete: SimpleCallable? = null, start: Int = 0) {
        val manager = Main.formManager
        for (i in start until forms.size) {
            val form = forms[i]
            val name = form.getName()

            if (manager.existsForm(name)) {
                confirmOverwrite(player, name) {
                    if (it) {
                        manager.addForm(form.getName(), form)
                    }
                    importForms(player, forms, onComplete, i + 1)
                }
                return
            }

            manager.addForm(name, form)
        }
        if (onComplete !== null) onComplete()
    }

    fun importForms(player: Player, forms: List<Form>, onComplete: SimpleCallable? = null) {
         importForms(player, forms, onComplete, 0)
    }

    fun importConfigs(player: Player, configs: LinkedHashMap<String, Map<String, Any?>>, onComplete: SimpleCallable? = null, start: Int = 0) {
        val names = configs.keys.toList()
        for (i in start until configs.size) {
            val name = names[i]
            val configData = configs[name]

            if (ConfigHolder.existsConfigFile(name)) {
                confirmOverwrite(player, "$name.yml") {
                    if (it) {
                        ConfigHolder.setConfig(name, LinkedHashMap(configData), true)
                    }
                    importConfigs(player, configs, onComplete, i + 1)
                }
                return
            }

            ConfigHolder.setConfig(name, LinkedHashMap(configData), true)
        }
        if (onComplete !== null) onComplete()
    }


    fun confirmOverwrite(player: Player, name: String, callback: (Boolean) -> Unit) {
        (ModalForm("@mineflow.import"))
            .setContent(Language.get("form.import.duplicate", listOf(name)))
            .setButton1("@form.yes")
            .setButton2("@form.no")
            .onReceive(callback)
            .show(player)
    }
}