package tokyo.aieuo.mineflow.ui.trigger

import cn.nukkit.Player
import tokyo.aieuo.mineflow.formAPI.ListForm
import tokyo.aieuo.mineflow.formAPI.ModalForm
import tokyo.aieuo.mineflow.formAPI.element.Button
import tokyo.aieuo.mineflow.recipe.Recipe
import tokyo.aieuo.mineflow.trigger.Trigger
import tokyo.aieuo.mineflow.trigger.Triggers
import tokyo.aieuo.mineflow.ui.RecipeForm
import tokyo.aieuo.mineflow.utils.Language

object BaseTriggerForm {

    fun sendAddedTriggerMenu(player: Player, recipe: Recipe, trigger: Trigger, messages: List<String> = listOf()) {
        val form = Triggers.getForm(trigger.type)
        if (form !== null) {
            form.sendAddedTriggerMenu(player, recipe, trigger)
            return
        }
        (ListForm(Language.get("form.trigger.addedTriggerMenu.title", listOf(recipe.name, trigger.toString()))))
            .setContent(trigger.toString())
            .addButtons(
                Button("@form.back") { RecipeForm.sendTriggerList(player, recipe) },
                Button("@form.delete") { sendConfirmDelete(player, recipe, trigger) },
            ).addMessages(messages).show(player)
    }

    fun sendSelectTriggerType(player: Player, recipe: Recipe) {
        (ListForm(Language.get("form.trigger.selectTriggerType", listOf(recipe.name))))
            .addButton(Button("@form.back") { RecipeForm.sendTriggerList(player, recipe) })
            .addButtonsEach(Triggers.getAllForm()) { form, type ->
                Button("@trigger.type.${type}") {
                    form.sendMenu(player, recipe)
                }
            }.show(player)
    }

    fun sendConfirmDelete(player: Player, recipe: Recipe, trigger: Trigger) {
        (ModalForm(Language.get("form.items.delete.title", listOf(recipe.name, trigger.toString()))))
            .setContent(Language.get("form.delete.confirm", listOf(trigger.toString())))
            .setButton1("@form.yes") {
                recipe.removeTrigger(trigger)
                RecipeForm.sendTriggerList(player, recipe, listOf("@form.deleted"))
            }.setButton2("@form.no") {
                sendAddedTriggerMenu(player, recipe, trigger, listOf("@form.cancelled"))
            }.show(player)
    }
}