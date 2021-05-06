package tokyo.aieuo.mineflow.ui.trigger

import cn.nukkit.Player
import tokyo.aieuo.mineflow.formAPI.CustomForm
import tokyo.aieuo.mineflow.formAPI.ListForm
import tokyo.aieuo.mineflow.formAPI.element.Button
import tokyo.aieuo.mineflow.formAPI.element.CancelToggle
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.recipe.Recipe
import tokyo.aieuo.mineflow.trigger.Trigger
import tokyo.aieuo.mineflow.trigger.Triggers
import tokyo.aieuo.mineflow.trigger.custom.CustomTrigger
import tokyo.aieuo.mineflow.ui.RecipeForm
import tokyo.aieuo.mineflow.utils.Language

object CustomTriggerForm : TriggerForm {

    override fun sendAddedTriggerMenu(player: Player, recipe: Recipe, trigger: Trigger, messages: List<String>) {
        (ListForm(Language.get("form.trigger.addedTriggerMenu.title", listOf(recipe.name, trigger.toString()))))
            .setContent(trigger.toString())
            .addButtons(
                Button("@form.back") { RecipeForm.sendTriggerList(player, recipe) },
                Button("@form.delete") { BaseTriggerForm.sendConfirmDelete(player, recipe, trigger) },
            ).addMessages(messages).show(player)
    }

    override fun sendMenu(player: Player, recipe: Recipe) {
        (CustomForm(Language.get("form.trigger.triggerMenu.title", listOf(recipe.name, Triggers.CUSTOM))))
            .addContents(
                mutableListOf(
                    ExampleInput("@trigger.custom.name", "aieuo", required = true),
                    CancelToggle { BaseTriggerForm.sendSelectTriggerType(player, recipe) },
                )
            ).onReceive { data ->
                val trigger = CustomTrigger.create(data.getString(0))
                if (recipe.existsTrigger(trigger)) {
                    sendAddedTriggerMenu(player, recipe, trigger, listOf("@trigger.alreadyExists"))
                    return@onReceive
                }
                recipe.addTrigger(trigger)
                sendAddedTriggerMenu(player, recipe, trigger, listOf("@trigger.add.success"))
            }.show(player)
    }
}