package tokyo.aieuo.mineflow.ui.trigger

import cn.nukkit.Player
import tokyo.aieuo.mineflow.formAPI.CustomForm
import tokyo.aieuo.mineflow.formAPI.ListForm
import tokyo.aieuo.mineflow.formAPI.element.Button
import tokyo.aieuo.mineflow.formAPI.element.CancelToggle
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleNumberInput
import tokyo.aieuo.mineflow.recipe.Recipe
import tokyo.aieuo.mineflow.trigger.Trigger
import tokyo.aieuo.mineflow.trigger.Triggers
import tokyo.aieuo.mineflow.trigger.time.TimeTrigger
import tokyo.aieuo.mineflow.ui.RecipeForm
import tokyo.aieuo.mineflow.utils.Language

object TimeTriggerForm : TriggerForm {

    override fun sendAddedTriggerMenu(player: Player, recipe: Recipe, trigger: Trigger, messages: List<String>) {
        (ListForm(
            Language.get(
                "form.trigger.addedTriggerMenu.title",
                listOf(recipe.name, "${trigger.key}:${trigger.subKey}")
            )
        ))
            .setContent(trigger.toString())
            .addButtons(
                Button("@form.back") { RecipeForm.sendTriggerList(player, recipe) },
                Button("@form.delete") { BaseTriggerForm.sendConfirmDelete(player, recipe, trigger) },
            ).addMessages(messages).show(player)
    }

    override fun sendMenu(player: Player, recipe: Recipe) {
        (CustomForm(Language.get("form.trigger.triggerMenu.title", listOf(recipe.name, Triggers.TIME))))
            .addContents(
                mutableListOf(
                    ExampleNumberInput("@trigger.time.hours", "12", required = true, min = 0.0, max = 23.0),
                    ExampleNumberInput("@trigger.time.minutes", "0", required = true, min = 0.0, max = 59.0),
                    CancelToggle { BaseTriggerForm.sendSelectTriggerType(player, recipe) },
                )
            ).onReceive { data ->
                val hour = data.getString(0)
                val minute = data.getString(1)
                val trigger = TimeTrigger.create(hour, minute)
                if (recipe.existsTrigger(trigger)) {
                    sendAddedTriggerMenu(player, recipe, trigger, listOf("@trigger.alreadyExists"))
                    return@onReceive
                }
                recipe.addTrigger(trigger)
                sendAddedTriggerMenu(player, recipe, trigger, listOf("@trigger.add.success"))
            }.show(player)
    }
}