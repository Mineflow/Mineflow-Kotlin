package tokyo.aieuo.mineflow.ui.trigger

import cn.nukkit.Player
import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.formAPI.ListForm
import tokyo.aieuo.mineflow.formAPI.element.Button
import tokyo.aieuo.mineflow.recipe.Recipe
import tokyo.aieuo.mineflow.trigger.Trigger
import tokyo.aieuo.mineflow.trigger.event.EventTrigger
import tokyo.aieuo.mineflow.ui.HomeForm
import tokyo.aieuo.mineflow.ui.MineflowForm
import tokyo.aieuo.mineflow.ui.RecipeForm
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.utils.Session

object EventTriggerForm : TriggerForm {

    override fun sendAddedTriggerMenu(player: Player, recipe: Recipe, trigger: Trigger, messages: List<String>) {
        (ListForm(Language.get("form.trigger.addedTriggerMenu.title", listOf(recipe.name, trigger.key))))
            .setContent(trigger.toString())
            .appendContent("@trigger.event.variable", true)
            .also { form ->
                Main.eventManager.getTrigger(trigger.key)?.getVariablesDummy()?.forEach { (name, variable) ->
                    form.appendContent("{$name} (type = ${variable.valueType})")
                }
            }.addButtons(
                Button("@form.back") { RecipeForm.sendTriggerList(player, recipe) },
                Button("@form.delete") { BaseTriggerForm.sendConfirmDelete(player, recipe, trigger) },
            ).addMessages(messages).show(player)
    }

    override fun sendMenu(player: Player, recipe: Recipe) {
        sendEventTriggerList(player, recipe)
    }

    fun sendEventTriggerList(player: Player, recipe: Recipe) {
        val events = Main.eventManager.getEnabledEvents()
        val buttons = mutableListOf(Button("@form.back") {
            BaseTriggerForm.sendSelectTriggerType(player, recipe)
        })

        for ((event, _) in events) {
            buttons.add(Button(EventTrigger.create(event).toString()) {
                sendSelectEventTrigger(player, recipe, event)
            })
        }

        (ListForm(Language.get("trigger.event.list.title", listOf(recipe.name))))
            .addButtons(buttons)
            .show(player)
    }

    fun sendSelectEventTrigger(player: Player, recipe: Recipe, eventName: String) {
        (ListForm(Language.get("trigger.event.select.title", listOf(recipe.name, eventName))))
            .setContent(EventTrigger.create(eventName).toString())
            .appendContent("@trigger.event.variable", true)
            .also { form ->
                Main.eventManager.getTrigger(eventName)?.getVariablesDummy()?.forEach { (name, variable) ->
                    form.appendContent("{${name}} (type = ${variable.valueType})")
                }
            }.addButtons(
                Button("@form.back") { sendEventTriggerList(player, recipe) },
                Button("@form.add") {
                    val trigger = EventTrigger.create(eventName)
                    if (recipe.existsTrigger(trigger)) {
                        sendAddedTriggerMenu(player, recipe, trigger, listOf("@trigger.alreadyExists"))
                        return@Button
                    }
                    recipe.addTrigger(trigger)
                    sendAddedTriggerMenu(player, recipe, trigger, listOf("@trigger.add.success"))
                },
            ).show(player)
    }

    fun sendSelectEvent(player: Player) {
        val events = Main.eventManager.getEnabledEvents()
        val buttons = mutableListOf(Button("@form.back") { HomeForm.sendMenu(player) })
        for ((event, _) in events) {
            buttons.add(Button(EventTrigger.create(event).toString()) {
                sendRecipeList(player, event)
            })
        }
        (ListForm("@form.event.list.title"))
            .addButtons(buttons)
            .show(player)
    }

    fun sendRecipeList(player: Player, event: String, messages: List<String> = listOf()) {
        val buttons = mutableListOf(
            Button("@form.back") { sendSelectEvent(player) },
            Button("@form.add") {
                MineflowForm.selectRecipe(player, Language.get(
                    "form.recipes.add", listOf(
                        Language.get("trigger.event.$event")
                    )
                ), { recipe ->
                    val trigger = EventTrigger.create(event)
                    if (recipe.existsTrigger(trigger)) {
                        sendRecipeList(player, event, listOf("@trigger.alreadyExists"))
                        return@selectRecipe
                    }
                    recipe.addTrigger(trigger)
                    sendRecipeList(player, event, listOf("@form.added"))
                }, {
                    sendRecipeList(player, event)
                })
            }
        )

        val recipes = Main.eventManager.getAssignedRecipes(event)
        for ((name, events) in recipes) {
            buttons.add(Button(name) { sendRecipeMenu(player, event, name) })
        }
        (ListForm(Language.get("form.recipes.title", listOf(EventTrigger.create(event).toString()))))
            .setButtons(buttons)
            .addMessages(messages)
            .show(player)
    }

    fun sendRecipeMenu(player: Player, event: String, recipeName: String) {
        (ListForm(Language.get("form.recipes.title", listOf(EventTrigger.create(event).toString()))))
            .setButtons(mutableListOf(
                Button("@form.back") { sendRecipeList(player, event) },
                Button("@form.edit") {
                    Session.getSession(player).set("recipe_menu_prev", fun() {
                        sendRecipeMenu(player, event, recipeName)
                    })
                    val (name, group) = Main.recipeManager.parseName(recipeName)
                    val recipe = Main.recipeManager.get(name, group) ?: return@Button
                    RecipeForm.sendTriggerList(player, recipe)
                }
            )).show(player)
    }
}
