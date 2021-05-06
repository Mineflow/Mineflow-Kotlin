package tokyo.aieuo.mineflow.ui

import cn.nukkit.Player
import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.exception.InvalidFormValueException
import tokyo.aieuo.mineflow.flowItem.FlowItemContainer
import tokyo.aieuo.mineflow.formAPI.CustomForm
import tokyo.aieuo.mineflow.formAPI.ListForm
import tokyo.aieuo.mineflow.formAPI.ModalForm
import tokyo.aieuo.mineflow.formAPI.element.*
import tokyo.aieuo.mineflow.recipe.Recipe
import tokyo.aieuo.mineflow.ui.trigger.BaseTriggerForm
import tokyo.aieuo.mineflow.utils.*
import java.nio.file.Files
import java.nio.file.Path

object RecipeForm {

    fun sendMenu(player: Player, messages: List<String> = listOf()) {
        (ListForm("@mineflow.recipe"))
            .addButtons(
                Button("@form.back") { HomeForm.sendMenu(player) },
                Button("@form.add") { sendAddRecipe(player) },
                Button("@form.edit") { sendSelectRecipe(player) },
                Button("@form.recipe.menu.recipeList") { sendRecipeList(player) },
                Button("@mineflow.export") {
                    MineflowForm.selectRecipe(player, "@form.export.selectRecipe.title",
                        { recipe -> ExportForm.sendRecipeListByRecipe(player, recipe) },
                        { sendMenu(player) }
                    )
                },
                Button("@mineflow.import") { ImportForm.sendSelectImportFile(player) },
            ).addMessages(messages).show(player)
    }

    fun sendAddRecipe(player: Player, inputDefaultName: String = "", inputDefaultGroup: String = "") {
        val manager = Main.recipeManager
        val defaultName = manager.getNotDuplicatedName("recipe")

        (CustomForm("@form.recipe.addRecipe.title"))
            .setContents(
                mutableListOf(
                    Input("@form.recipe.recipeName", defaultName, inputDefaultName),
                    Input("@form.recipe.groupName", "", inputDefaultGroup),
                    CancelToggle { sendMenu(player) },
                )
            ).onReceive { data ->
                val name = data.getString(0).let { if (it.isEmpty()) defaultName else it }
                val group = data.getString(1)

                if (name.contains(Regex("[.¥/:?<>|*\"]"))) throw InvalidFormValueException(
                    "@form.recipe.invalidName", 0
                )
                if (group.contains(Regex("[.¥:?<>|*\"]"))) throw InvalidFormValueException(
                    "@form.recipe.invalidName", 1
                )

                if (manager.exists(name, group)) {
                    val newName = manager.getNotDuplicatedName(name, group)
                    MineflowForm.confirmRename(player, name, newName, {
                        val recipe = Recipe(name, group, player.name, Main.pluginVersion)
                        manager.add(recipe)
                        Session.getSession(player).set("recipe_menu_prev") {
                            sendRecipeList(player, recipe.group)
                        }
                        sendRecipeMenu(player, recipe)
                    }, {
                        resend(Language.get("form.recipe.exists", listOf(name)) to 0)
                    })
                    return@onReceive
                }

                val recipe = Recipe(name, group, player.name, Main.pluginVersion)
                if (Files.exists(Path.of((recipe.getFileName(manager.saveDir))))) {
                    resend(Language.get("form.recipe.exists", listOf(name)) to 0)
                    return@onReceive
                }

                manager.add(recipe)
                Session.getSession(player).set("recipe_menu_prev", {
                    sendRecipeList(player, recipe.group)
                })
                sendRecipeMenu(player, recipe)
            }.show(player)
    }

    fun sendSelectRecipe(player: Player, defaultName: String = "") {
        MineflowForm.selectRecipe(player, "@form.recipe.select.title", { recipe ->
            Session.getSession(player).set("recipe_menu_prev", fun() {
                sendRecipeList(player, recipe.group)
            })
            sendRecipeMenu(player, recipe)
        }, {
            sendMenu(player)
        }, defaultName)
    }

    fun sendRecipeList(player: Player, path: String = "", messages: List<String> = listOf()) {
        val manager = Main.recipeManager
        val recipeGroups = manager.getByPath(path).toMutableMap()
        val buttons = mutableListOf(
            Button("@form.back") {
                if (path == "") {
                    sendMenu(player)
                    return@Button
                }
                val paths = ArrayDeque(path.split("/"))
                paths.removeLastOrNull()
                sendRecipeList(player, paths.joinToString("/"))
            },
            Button("@recipe.add") { sendAddRecipe(player, "", path) },
        )

        val recipes = recipeGroups[path]?.toMutableMap() ?: mutableMapOf()
        for (recipe in recipes.values) {
            buttons.add(Button(recipe.name) {
                Session.getSession(player).set("recipe_menu_prev", { sendRecipeList(player, path) })
                sendRecipeMenu(player, recipe)
            })
        }
        recipeGroups.remove(path)

        val groups = mutableListOf<String>()
        for ((group, _) in recipeGroups) {
            val name = if (path != "") {
                group.replace("$path/", "")
            } else {
                group
            }.let { it.split("/").firstOrNull() ?: it }

            if (!groups.contains(name)) {
                buttons.add(Button("[$name]") { sendRecipeList(player, if (path != "") "${path}/${name}" else name) })
                groups.add(name)
            }
        }
        if (path != "") buttons.add(Button("@recipe.group.delete") { confirmDeleteRecipeGroup(player, path) })

        (ListForm("@form.recipe.recipeList.title"))
            .addButtons(buttons)
            .addMessages(messages)
            .show(player)
    }

    fun sendRecipeMenu(player: Player, recipe: Recipe, messages: List<String> = listOf()) {
        val detail = recipe.getDetail().trim()
        (ListForm(Language.get("form.recipe.recipeMenu.title", listOf(recipe.getPathname()))))
            .setContent(if (detail.isEmpty()) "@recipe.noActions" else detail)
            .addButtons(
                Button("@form.back"),
                Button("@action.edit"),
                Button("@form.recipe.recipeMenu.changeName"),
                Button("@form.recipe.recipeMenu.execute"),
                Button("@form.recipe.recipeMenu.setTrigger"),
                Button("@form.recipe.args.return.set"),
                Button("@form.recipe.changeTarget"),
                Button("@form.recipe.recipeMenu.save"),
                Button("@mineflow.export"),
                Button("@form.delete"),
            ).onReceive { data ->
                when (data) {
                    0 -> {
                        val prev = Session.getSession(player).get<SimpleCallable>("recipe_menu_prev")
                        if (prev !== null) prev() else sendMenu(player)
                    }
                    1 -> {
                        Session.getSession(player).set("parents", ArrayDeque<FlowItemContainer>())
                        FlowItemContainerForm.sendActionList(player, recipe, FlowItemContainer.ACTION)
                    }
                    2 -> sendChangeName(player, recipe)
                    3 -> recipe.executeAllTargets(player)
                    4 -> sendTriggerList(player, recipe)
                    5 -> (ListForm("@form.recipe.args.return.set"))
                        .setButtons(
                            mutableListOf(
                                Button("@form.back") { sendRecipeMenu(player, recipe) },
                                Button("@form.recipe.args.set") { sendSetArgs(player, recipe) },
                                Button("@form.recipe.returnValue.set") { sendSetReturns(player, recipe) },
                            )
                        ).show(player)
                    6 -> sendChangeTarget(player, recipe)
                    7 -> {
                        recipe.save(Main.recipeManager.saveDir)
                        sendRecipeMenu(player, recipe, listOf("@form.recipe.recipeMenu.save.success"))
                    }
                    8 -> ExportForm.sendRecipeListByRecipe(player, recipe)
                    9 -> (ModalForm(Language.get("form.recipe.delete.title", listOf(recipe.name))))
                        .setContent(Language.get("form.delete.confirm", listOf(recipe.name)))
                        .onYes {
                            recipe.removeTriggerAll()
                            Main.recipeManager.remove(recipe.name, recipe.group)
                            sendRecipeList(player, recipe.group, listOf("@form.deleted"))
                        }.onNo {
                            sendRecipeMenu(player, recipe, listOf("@form.cancelled"))
                        }.show(player)
                }
            }.addMessages(messages).show(player)
    }

    fun sendChangeName(player: Player, recipe: Recipe) {
        CustomForm(Language.get("form.recipe.changeName.title", listOf(recipe.name)))
            .setContents(mutableListOf(
                Label("@form.recipe.changeName.content0"),
                Input("@form.recipe.changeName.content1", default = recipe.name, required = true),
                CancelToggle { sendRecipeMenu(player, recipe, listOf("@form.cancelled")) }
            )).onReceive { data ->
                val name = data.getString(1)

                val manager = Main.recipeManager
                if (manager.exists(name, recipe.group)) {
                    val newName = manager.getNotDuplicatedName(name, recipe.group)
                    MineflowForm.confirmRename(player, name, newName, {
                        manager.rename(recipe.name, it, recipe.group)
                        sendRecipeMenu(player, recipe)
                    }, {
                        resend(Language.get("form.recipe.exists", listOf(it)) to 1)
                    })
                    return@onReceive
                }
                manager.rename(recipe.name, name, recipe.group)
                sendRecipeMenu(player, recipe, listOf("@form.recipe.changeName.success"))
            }.show(player)
    }

    fun sendTriggerList(player: Player, recipe: Recipe, messages: List<String> = listOf()) {
        val triggers = recipe.triggers
        (ListForm(Language.get("form.recipe.triggerList.title", listOf(recipe.name))))
            .addButton(Button("@form.back") { sendRecipeMenu(player, recipe) })
            .addButton(Button("@form.add") { BaseTriggerForm.sendSelectTriggerType(player, recipe) })
            .addButtonsEach(triggers) { trigger ->
                Button(trigger.toString()) {
                    BaseTriggerForm.sendAddedTriggerMenu(player, recipe, trigger)
                }
            }.addMessages(messages).show(player)
    }

    fun sendSetArgs(player: Player, recipe: Recipe, messages: List<String> = listOf()) {
        val contents = mutableListOf<Element>(CancelToggle({ sendRecipeMenu(player, recipe) }, "@form.exit"))
        for ((i, argument) in recipe.arguments.withIndex()) {
            contents.add(Input(Language.get("form.recipe.args", listOf(i.toString())), "", argument))
        }
        contents.add(Input("@form.recipe.args.add"))
        (CustomForm("@form.recipe.args.set"))
            .setContents(contents)
            .onReceive { data ->
                val arguments = mutableListOf<String>()
                for (i in 1 until data.size) {
                    data.getString(i).let {
                        if (it != "") arguments.add(it)
                    }
                }
                recipe.arguments = arguments
                sendSetArgs(player, recipe, listOf("@form.changed"))
            }.onClose {
                sendRecipeMenu(player, recipe)
            }.addMessages(messages).show(player)
    }

    fun sendSetReturns(player: Player, recipe: Recipe, messages: List<String> = listOf()) {
        val contents = mutableListOf<Element>(CancelToggle({ sendRecipeMenu(player, recipe) }, "@form.exit"))
        for ((i, value) in recipe.returnValues.withIndex()) {
            contents.add(Input(Language.get("form.recipe.returnValue", listOf(i.toString())), default = value))
        }
        contents.add(Input("@form.recipe.returnValue.add"))
        (CustomForm("@form.recipe.returnValue.set"))
            .setContents(contents)
            .onReceive { data ->
                val returnValues = mutableListOf<String>()
                for (i in 1 until data.size) {
                    data.getString(i).let {
                        if (it != "") returnValues.add(it)
                    }
                }
                recipe.returnValues = returnValues
                sendSetReturns(player, recipe, listOf("@form.changed"))
            }.onClose {
                sendRecipeMenu(player, recipe)
            }.addMessages(messages).show(player)
    }

    fun sendChangeTarget(player: Player, recipe: Recipe) {
        val default1 = if (recipe.targetType == Recipe.TARGET_SPECIFIED) {
            recipe.getTargetOption("specified", listOf<String>()).joinToString(",")
        } else {
            ""
        }
        val default2 = if (recipe.targetType == Recipe.TARGET_RANDOM) {
            recipe.getTargetOption("random", 1).toString()
        } else {
            ""
        }
        (CustomForm(Language.get("form.recipe.changeTarget.title", listOf(recipe.name)))).setContents(mutableListOf(
            Dropdown(
                "@form.recipe.changeTarget.type", listOf(
                    Language.get("form.recipe.target.none"),
                    Language.get("form.recipe.target.default"),
                    Language.get("form.recipe.target.specified"),
                    Language.get("form.recipe.target.onWorld"),
                    Language.get("form.recipe.target.all"),
                    Language.get("form.recipe.target.random"),
                ), recipe.targetType
            ),
            Input("@form.recipe.changeTarget.name", "@form.recipe.changeTarget.name.placeholder", default1),
            NumberInput(
                "@form.recipe.changeTarget.random",
                "@form.recipe.changeTarget.random.placeholder",
                default2
            ),
            CancelToggle { sendRecipeMenu(player, recipe, listOf("@form.cancelled")) }
        )).onReceive { data ->
            val type = data.getInt(0)
            if (type == Recipe.TARGET_SPECIFIED && data.getString(1) == "") {
                resend("@form.insufficient" to 1)
                return@onReceive
            }
            if (type == Recipe.TARGET_RANDOM && data.getString(2) == "") {
                resend("@form.insufficient" to 2)
                return@onReceive
            }

            when (type) {
                Recipe.TARGET_SPECIFIED -> recipe.setTargetSetting(
                    type, mapOf(
                        "specified" to data.getString(1).split(",")
                    )
                )
                Recipe.TARGET_RANDOM -> recipe.setTargetSetting(
                    type, mapOf(
                        "random" to (data.getIntOrNull(2) ?: 1)
                    )
                )
                else -> recipe.setTargetSetting(type)
            }
            sendRecipeMenu(player, recipe, listOf("@form.changed"))
        }.show(player)
    }

    fun confirmDeleteRecipeGroup(player: Player, path: String) {
        val recipes = Main.recipeManager.getByPath(path).toMap()
        val count = recipes.size - 1 + (recipes[path]?.size ?: 0)
        if (count >= 1) {
            sendRecipeList(player, path, listOf("@recipe.group.delete.not.empty"))
            return
        }
        (ModalForm(Language.get("form.recipe.delete.title", listOf(path))))
            .setContent(Language.get("form.delete.confirm", listOf(path, recipes.size.toString())))
            .onYes {
                val manager = Main.recipeManager
                val result = manager.deleteGroup(path)
                sendRecipeList(
                    player,
                    manager.getParentPath(path),
                    listOf(if (result) "@form.deleted" else "@recipe.group.delete.not.empty")
                )
            }.onNo {
                sendRecipeList(player, path, listOf("@form.cancelled"))
            }.show(player)
    }
}