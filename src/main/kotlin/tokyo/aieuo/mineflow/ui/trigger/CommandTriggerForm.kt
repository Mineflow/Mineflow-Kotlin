package tokyo.aieuo.mineflow.ui.trigger

import cn.nukkit.Player
import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.formAPI.CustomForm
import tokyo.aieuo.mineflow.formAPI.ListForm
import tokyo.aieuo.mineflow.formAPI.ModalForm
import tokyo.aieuo.mineflow.formAPI.element.Button
import tokyo.aieuo.mineflow.formAPI.element.CancelToggle
import tokyo.aieuo.mineflow.formAPI.element.Input
import tokyo.aieuo.mineflow.recipe.Recipe
import tokyo.aieuo.mineflow.trigger.Trigger
import tokyo.aieuo.mineflow.trigger.command.CommandTrigger
import tokyo.aieuo.mineflow.ui.CommandForm
import tokyo.aieuo.mineflow.ui.RecipeForm
import tokyo.aieuo.mineflow.utils.Language

object CommandTriggerForm: TriggerForm {

    override fun sendAddedTriggerMenu(player: Player, recipe: Recipe, trigger: Trigger, messages: List<String>) {
        (ListForm(Language.get("form.trigger.addedTriggerMenu.title", listOf(recipe.name, trigger.key))))
            .setContent(trigger.toString())
            .addButtons(
                Button("@form.back") { RecipeForm.sendTriggerList(player, recipe) },
                Button("@form.delete") { BaseTriggerForm.sendConfirmDelete(player, recipe, trigger) },
                Button("@trigger.command.edit.title") {
                    val manager = Main.commandManager
                    val command = manager.getCommand(manager.getOriginCommand(trigger.key))
                    CommandForm.sendCommandMenu(player, command ?: return@Button)
                },
            ).addMessages(messages).show(player)
    }

    override fun sendMenu(player: Player, recipe: Recipe) {
        sendSelectCommand(player, recipe)
    }

    fun sendSelectCommand(player: Player, recipe: Recipe) {
        (CustomForm(Language.get("trigger.command.select.title", listOf(recipe.name))))
            .setContents(mutableListOf(
                Input("@trigger.command.select.input", "@trigger.command.select.placeholder", required = true),
                CancelToggle { BaseTriggerForm.sendSelectTriggerType(player, recipe) },
            )).onReceive { data ->
                val command = data.getString(0)

                val manager = Main.commandManager
                val original = manager.getOriginCommand(command)
                if (!manager.existsCommand(original)) {
                    sendConfirmCreate(player, original) { result ->
                        if (result) {
                            CommandForm.sendAddCommand(player, command)
                        } else {
                            resend("@trigger.command.select.notFound" to 0)
                            return@sendConfirmCreate
                        }
                    }
                    return@onReceive
                }

                val trigger = CommandTrigger.create(command.split(" ").firstOrNull() ?: command, command)
                if (recipe.existsTrigger(trigger)) {
                    sendAddedTriggerMenu(player, recipe, trigger, listOf("@trigger.alreadyExists"))
                    return@onReceive
                }
                recipe.addTrigger(trigger)
                sendAddedTriggerMenu(player, recipe, trigger, listOf("@trigger.add.success"))
            }.show(player)
    }

    fun sendConfirmCreate(player: Player, name: String, callback: (Boolean) -> Unit) {
        (ModalForm("@trigger.command.confirmCreate.title"))
            .setContent(Language.get("trigger.command.confirmCreate.content", listOf(name)))
            .setButton1("@form.yes")
            .setButton2("@form.no")
            .onReceive { data ->
                callback(data)
            }.show(player)
    }
}