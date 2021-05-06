package tokyo.aieuo.mineflow.ui.trigger

import cn.nukkit.Player
import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.formAPI.*
import tokyo.aieuo.mineflow.formAPI.element.Button
import tokyo.aieuo.mineflow.formAPI.element.CancelToggle
import tokyo.aieuo.mineflow.formAPI.element.Input
import tokyo.aieuo.mineflow.recipe.Recipe
import tokyo.aieuo.mineflow.trigger.Trigger
import tokyo.aieuo.mineflow.trigger.form.FormTrigger
import tokyo.aieuo.mineflow.ui.RecipeForm
import tokyo.aieuo.mineflow.ui.customForm.CustomFormForm
import tokyo.aieuo.mineflow.utils.Language

object FormTriggerForm : TriggerForm {

    override fun sendAddedTriggerMenu(player: Player, recipe: Recipe, trigger: Trigger, messages: List<String>) {
        (ListForm(Language.get("form.trigger.addedTriggerMenu.title", listOf(recipe.name, trigger.key))))
            .setContent(trigger.toString())
            .addButtons(
                Button("@form.back") { RecipeForm.sendTriggerList(player, recipe) },
                Button("@form.delete") { BaseTriggerForm.sendConfirmDelete(player, recipe, trigger) },
                Button("@trigger.form.edit.title") {
                    val form = Main.formManager.getForm(trigger.key) ?: return@Button
                    CustomFormForm.sendFormMenu(player, form)
                },
            ).addMessages(messages).show(player)
    }

    override fun sendMenu(player: Player, recipe: Recipe) {
        sendSelectForm(player, recipe)
    }

    fun sendSelectForm(player: Player, recipe: Recipe) {
        (CustomForm(Language.get("trigger.form.select.title", listOf(recipe.name))))
            .setContents(
                mutableListOf(
                    Input("@trigger.form.select.input", required = true),
                    CancelToggle { BaseTriggerForm.sendSelectTriggerType(player, recipe) },
                )
            ).onReceive { data ->
                val name = data.getString(0)
                val manager = Main.formManager
                val form = manager.getForm(name)

                if (form === null) {
                    sendConfirmCreate(player, name) { result ->
                        if (result) {
                            CustomFormForm.sendAddForm(player, name)
                        } else {
                            resend("@trigger.form.select.notFound" to 0)
                        }
                    }
                    return@onReceive
                }

                sendSelectFormTriggerButton(player, recipe, form)
            }.show(player)
    }

    fun sendSelectFormTriggerButton(player: Player, recipe: Recipe, form: Form) {
        when (form) {
            is CustomForm -> {
                (ListForm(Language.get("trigger.form.type.select", listOf(form.getName()))))
                    .addButtons(
                        Button("@form.cancelAndBack") { sendSelectForm(player, recipe) },
                        Button("@trigger.form.receive"),
                        Button("@trigger.form.close"),
                    ).onReceive { data ->
                        val trigger = FormTrigger.create(form.getName())
                        if (data == 2) {
                            trigger.subKey = "close"
                        }
                        if (recipe.existsTrigger(trigger)) {
                            sendAddedTriggerMenu(player, recipe, trigger, listOf("@trigger.alreadyExists"))
                            return@onReceive
                        }
                        recipe.addTrigger(trigger)
                        sendAddedTriggerMenu(player, recipe, trigger, listOf("@trigger.add.success"))
                    }.show(player)
            }
            is ModalForm -> {
                (ListForm(Language.get("trigger.form.type.select", listOf(form.getName()))))
                    .addButtons(
                        Button("@form.cancelAndBack") { sendSelectForm(player, recipe) },
                        Button("@trigger.form.receive"),
                        Button(Language.get("trigger.form.button", listOf(form.getButton1Text()))),
                        Button(Language.get("trigger.form.button", listOf(form.getButton2Text()))),
                    ).onReceive { data ->
                        val trigger = FormTrigger.create(form.getName())
                        when (data) {
                            2 -> trigger.subKey = "1"
                            3 -> trigger.subKey = "2"
                        }
                        if (recipe.existsTrigger(trigger)) {
                            sendAddedTriggerMenu(player, recipe, trigger, listOf("@trigger.alreadyExists"))
                            return@onReceive
                        }
                        recipe.addTrigger(trigger)
                        sendAddedTriggerMenu(player, recipe, trigger, listOf("@trigger.add.success"))
                    }.show(player)
            }
            is ListForm -> {
                val buttons = mutableListOf(
                    Button("@form.cancelAndBack") { sendSelectForm(player, recipe) },
                    Button("@trigger.form.receive"),
                    Button("@trigger.form.close"),
                )
                for (button in form.buttons) {
                    buttons.add(Button(Language.get("trigger.form.button", listOf(button.text))))
                }
                (ListForm(Language.get("trigger.form.type.select", listOf(form.getName()))))
                    .addButtons(buttons)
                    .onReceive { data ->
                        val trigger = FormTrigger.create(form.getName())
                        when (data) {
                            1 -> {
                            }
                            2 -> trigger.subKey = "close"
                            else -> {
                                val button = form.getButton(data - 3) ?: return@onReceive
                                trigger.subKey = button.getUUID()
                            }
                        }
                        if (recipe.existsTrigger(trigger)) {
                            sendAddedTriggerMenu(player, recipe, trigger, listOf("@trigger.alreadyExists"))
                            return@onReceive
                        }
                        recipe.addTrigger(trigger)
                        sendAddedTriggerMenu(player, recipe, trigger, listOf("@trigger.add.success"))
                    }.show(player)
            }
        }
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