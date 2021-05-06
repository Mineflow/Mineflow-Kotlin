package tokyo.aieuo.mineflow.ui

import cn.nukkit.Player
import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.command.CustomCommandData
import tokyo.aieuo.mineflow.formAPI.CustomForm
import tokyo.aieuo.mineflow.formAPI.ListForm
import tokyo.aieuo.mineflow.formAPI.ModalForm
import tokyo.aieuo.mineflow.formAPI.element.Button
import tokyo.aieuo.mineflow.formAPI.element.CancelToggle
import tokyo.aieuo.mineflow.formAPI.element.Dropdown
import tokyo.aieuo.mineflow.formAPI.element.Input
import tokyo.aieuo.mineflow.trigger.command.CommandTrigger
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.utils.Session
import tokyo.aieuo.mineflow.utils.SimpleCallable

object CommandForm {

    fun sendMenu(player: Player, messages: List<String> = listOf()) {
        (ListForm("@form.command.menu.title"))
            .addButtons(
                Button("@form.back") { HomeForm.sendMenu(player) },
                Button("@form.add") { sendAddCommand(player) },
                Button("@form.edit") { sendSelectCommand(player) },
                Button("@form.command.menu.commandList") { sendCommandList(player) },
            ).addMessages(messages).show(player)
    }

    fun sendAddCommand(player: Player, defaultTitle: String = "", defaultDesc: String = "", defaultPerm: Int = 0) {
        (CustomForm("@form.command.addCommand.title"))
            .setContents(
                mutableListOf(
                    Input("@form.command.menu.title", "@trigger.command.select.placeholder", defaultTitle, true),
                    Input("@form.command.description", default = defaultDesc),
                    Dropdown(
                        "@form.command.permission", listOf(
                            Language.get("form.command.addCommand.permission.op"),
                            Language.get("form.command.addCommand.permission.true"),
                            Language.get("form.command.addCommand.permission.custom"),
                        ), defaultPerm
                    ),
                    CancelToggle { sendMenu(player) },
                )
            ).onReceive { data ->
                val command = data.getString(0)

                val manager = Main.commandManager
                val original = manager.getOriginCommand(command)
                if (!manager.isSubcommand(command) && manager.existsCommand(original)) {
                    resend("@form.command.alreadyExists" to 0)
                    return@onReceive
                }
                if (manager.isRegistered(original)) {
                    resend("@form.command.alreadyUsed" to 0)
                    return@onReceive
                }

                val permission = when (data.getInt(2)) {
                    0 -> "mineflow.customcommand.op"
                    1 -> "mineflow.customcommand.true"
                    else -> ""
                }

                manager.addCommand(command, permission, data[1] as String)
                val commandData = manager.getCommand(original) ?: return@onReceive
                Session.getSession(player).set("command_menu_prev") {
                    sendMenu(player)
                }

                if (data.getInt(2) == 2) {
                    sendSelectPermissionName(player, commandData)
                    return@onReceive
                }
                sendCommandMenu(player, commandData)
            }.show(player)
    }

    fun sendSelectCommand(player: Player, defaultCommand: String = "") {
        (CustomForm("@form.command.select.title"))
            .setContents(
                mutableListOf(
                    Input("@form.command.name", default = defaultCommand, required = true),
                    CancelToggle { sendMenu(player) },
                )
            ).onReceive { data ->
                val command = data.getString(0)
                val manager = Main.commandManager
                val commandData = manager.getCommand(manager.getOriginCommand(command))
                if (commandData === null) {
                    resend("@form.command.notFound" to 0)
                    return@onReceive
                }

                Session.getSession(player).set("command_menu_prev") {
                    sendSelectCommand(player)
                }
                sendCommandMenu(player, commandData)
            }.show(player)
    }

    fun sendCommandList(player: Player) {
        val manager = Main.commandManager
        val commands = manager.commandData
        val buttons = mutableListOf(Button("@form.back") { sendMenu(player) })
        for (command in commands.values) {
            buttons.add(Button("/${command.command}") {
                Session.getSession(player).set("command_menu_prev") {
                    sendCommandList(player)
                }
                sendCommandMenu(player, command)
            })
        }

        (ListForm("@form.command.commandList.title"))
            .addButtons(buttons)
            .show(player)
    }

    fun sendCommandMenu(player: Player, command: CustomCommandData, messages: List<String> = listOf()) {
        val permissions = mapOf(
            "mineflow.customcommand.op" to "@form.command.addCommand.permission.op",
            "mineflow.customcommand.true" to "@form.command.addCommand.permission.true"
        )
        val permission = permissions[command.permission] ?: command.permission
        ListForm("/${command.command}")
            .setContent(
                "/${command.command}\n"
                        + "${Language.get("form.command.permission")}: ${permission}\n"
                        + "${Language.get("form.command.description")}: ${command.description}"
            ).addButtons(
                Button("@form.back") {
                    val prev = Session.getSession(player).get<SimpleCallable>("command_menu_prev")
                    if (prev !== null) prev() else sendMenu(player)
                },
                Button("@form.command.commandMenu.editDescription") { changeDescription(player, command) },
                Button("@form.command.commandMenu.editPermission") { changePermission(player, command) },
                Button("@form.command.recipes") { sendRecipeList(player, command) },
                Button("@form.delete") { sendConfirmDelete(player, command) },
            ).addMessages(messages).show(player)
    }

    fun changeDescription(player: Player, command: CustomCommandData) {
        (CustomForm(Language.get("form.command.changeDescription.title", listOf("/${command.command}"))))
            .setContents(
                mutableListOf(
                    Input("@form.command.description", default = command.description),
                    CancelToggle { sendCommandMenu(player, command) },
                )
            ).onReceive { data ->
                command.description = data.getString(0)
                Main.commandManager.updateCommand(command)
                sendCommandMenu(player, command)
            }.show(player)
    }

    fun changePermission(player: Player, command: CustomCommandData) {
        val permissions = mutableMapOf("mineflow.customcommand.op" to 0, "mineflow.customcommand.true" to 1)
        (CustomForm(Language.get("form.command.changePermission.title", listOf("/${command.command}"))))
            .setContents(
                mutableListOf(
                    Dropdown(
                        "@form.command.permission", listOf(
                            Language.get("form.command.addCommand.permission.op"),
                            Language.get("form.command.addCommand.permission.true"),
                            Language.get("form.command.addCommand.permission.custom"),
                        ), permissions[command.permission] ?: 2
                    ),
                    CancelToggle { sendCommandMenu(player, command) },
                )
            ).onReceive { data ->
                val index = data.getInt(0)
                if (index == 2) {
                    sendSelectPermissionName(player, command)
                    return@onReceive
                }

                command.permission = if (index == 0) "mineflow.customcommand.op" else "mineflow.customcommand.true"
                Main.commandManager.updateCommand(command)
                sendCommandMenu(player, command)
            }.show(player)
    }

    fun sendSelectPermissionName(player: Player, command: CustomCommandData) {
        (CustomForm(Language.get("form.command.changePermission.title", listOf("/${command.command}"))))
            .setContents(
                mutableListOf(
                    Input(
                        "@form.command.addCommand.permission.custom.input",
                        default = command.permission,
                        required = true
                    ),
                    CancelToggle { changePermission(player, command) },
                )
            ).onReceive { data ->
                command.permission = data.getString(0)
                Main.commandManager.updateCommand(command)
                sendCommandMenu(player, command)
            }.show(player)
    }

    fun sendConfirmDelete(player: Player, command: CustomCommandData) {
        (ModalForm(Language.get("form.command.delete.title", listOf("/${command.command}"))))
            .setContent(Language.get("form.delete.confirm", listOf("/${command.command}")))
            .setButton1("@form.yes") {
                val commandManager = Main.commandManager
                val recipeManager = Main.recipeManager

                val recipes = Main.commandManager.getAssignedRecipes(command.command)
                for ((_name, commands) in recipes) {
                    val (name, group) = recipeManager.parseName(_name)

                    val recipe = recipeManager.get(name, group)
                    if (recipe === null) continue

                    for (cmd in commands) {
                        recipe.removeTrigger(CommandTrigger.create(cmd.split(" ").first(), cmd))
                    }
                }
                commandManager.removeCommand(command.command)
                sendMenu(player, listOf("@form.deleted"))
            }.setButton2("@form.no") {
                sendCommandMenu(player, command, listOf("@form.cancelled"))
            }.show(player)
    }

    fun sendRecipeList(player: Player, command: CustomCommandData, messages: List<String> = listOf()) {
        val buttons = mutableListOf(
            Button("@form.back"),
            Button("@form.add")
        )

        val recipes = Main.commandManager.getAssignedRecipes(command.command)
        for ((name, commands) in recipes) {
            buttons.add(Button("$name | ${commands.size}"))
        }
        (ListForm(Language.get("form.recipes.title", listOf("/${command.command}"))))
            .setButtons(buttons)
            .onReceive { data ->
                when (data) {
                    0 -> sendCommandMenu(player, command)
                    1 -> {
                        MineflowForm.selectRecipe(player,
                            Language.get("form.recipes.add", listOf(command.command)),
                            { recipe ->
                                val trigger = CommandTrigger(command.command, command.command)
                                if (recipe.existsTrigger(trigger)) {
                                    sendRecipeList(player, command, listOf("@trigger.alreadyExists"))
                                    return@selectRecipe
                                }
                                recipe.addTrigger(trigger)
                                sendRecipeList(player, command, listOf("@form.added"))
                            },
                            {
                                sendRecipeList(player, command)
                            }
                        )
                    }
                    else -> sendRecipeMenu(player, command, data - 2, recipes)
                }
            }.addMessages(messages).show(player)
    }

    fun sendRecipeMenu(player: Player, commandData: CustomCommandData, index: Int, recipes: Map<String, List<String>>) {
        val command = Main.commandManager.getCommand(commandData.command) ?: return
        val triggers = recipes.values.toList()[index]
        val content = triggers.joinToString("\n") { "/$it" }
        (ListForm(Language.get("form.recipes.title", listOf("/${command.command}"))))
            .setContent(content)
            .setButtons(mutableListOf(
                Button("@form.back") { sendRecipeList(player, command) },
                Button("@form.edit") {
                    Session.getSession(player).set("recipe_menu_prev", fun() {
                        sendRecipeMenu(player, command, index, recipes)
                    })
                    val recipeName = recipes.keys.toList()[index]
                    val (name, group) = Main.recipeManager.parseName(recipeName)
                    val recipe = Main.recipeManager.get(name, group) ?: return@Button
                    RecipeForm.sendTriggerList(player, recipe)
                }
            )).show(player)
    }
}