package tokyo.aieuo.mineflow.ui.customForm

import cn.nukkit.Player
import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.formAPI.CustomForm
import tokyo.aieuo.mineflow.formAPI.ListForm
import tokyo.aieuo.mineflow.formAPI.element.Button
import tokyo.aieuo.mineflow.formAPI.element.CancelToggle
import tokyo.aieuo.mineflow.formAPI.element.Input
import tokyo.aieuo.mineflow.formAPI.element.Label
import tokyo.aieuo.mineflow.formAPI.element.mineflow.CommandButton
import tokyo.aieuo.mineflow.formAPI.utils.ButtonImage
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.utils.Session
import tokyo.aieuo.mineflow.utils.SimpleCallable

object CustomListFormForm {

    fun sendMenu(player: Player, form: ListForm, messages: List<String> = listOf()) {
        (ListForm(form.getName()))
            .addButton(
                Button("@form.back") {
                    val prev = Session.getSession(player).get<SimpleCallable>("form_menu_prev")
                    if (prev !== null) prev() else (CustomFormForm).sendMenu(player)
                }
            ).addButton(
                Button("@form.form.formMenu.preview") {
                    form.onReceive { _ ->
                        sendMenu(player, form)
                    }.onClose {
                        sendMenu(player, form)
                    }.show(player)
                }
            ).addButton(
                Button("@form.recipe.recipeMenu.execute") {
                    form.onReceive { data ->
                        CustomFormForm.onReceive(player, data, form)
                    }.onClose {
                        CustomFormForm.onClose(player, form)
                    }.show(player)
                }
            ).addButton(
                Button("@form.form.formMenu.changeTitle") {
                    CustomFormForm.sendChangeFormTitle(player, form)
                }
            ).addButton(
                Button("@form.form.formMenu.editContent") {
                    CustomFormForm.sendChangeFormContent(player, form)
                }
            ).addButton(
                Button("@customForm.list.editButton") {
                    sendButtonList(player, form)
                }
            ).addButton(
                Button("@form.form.formMenu.changeName") {
                    CustomFormForm.sendChangeFormName(player, form)
                }
            ).addButton(
                Button("@form.form.recipes") {
                    CustomFormForm.sendRecipeList(player, form)
                }
            ).addButton(
                Button("@form.delete") {
                    CustomFormForm.sendConfirmDelete(player, form)
                }
            ).addMessages(messages).show(player)
    }

    fun sendButtonList(player: Player, form: ListForm, messages: List<String> = listOf()) {
        (ListForm("@customForm.list.editButton"))
            .addButton(Button("@form.back") { sendMenu(player, form) })
            .addButton(Button("@customForm.list.addButton") { sendSelectButtonType(player, form) })
            .addButtonsEach(form.buttons) { button, i ->
                Button(button.toString()) {
                    sendEditButton(player, form, button, i)
                }
            }.addMessages(messages).show(player)

    }

    fun sendSelectButtonType(player: Player, form: ListForm) {
        (ListForm("@customForm.list.addButton"))
            .setButtons(
                mutableListOf(
                    Button("@form.back") { sendMenu(player, form) },
                    Button("@customForm.list.button.type.normal") { sendAddButton(player, form) },
                    Button("@customForm.list.button.type.command") { sendAddCommandButton(player, form) },
                )
            ).show(player)
    }

    fun sendAddButton(player: Player, form: ListForm) {
        (CustomForm("@customForm.list.addButton"))
            .setContents(
                mutableListOf(
                    Input("@customForm.text", required = true),
                    Input("@customForm.image", Language.get("form.example", listOf("textures/items/apple"))),
                    CancelToggle { sendButtonList(player, form, listOf("@form.canceled")) },
                )
            ).onReceive { data ->
                val image = data.getString(1).let { if (it == "") null else ButtonImage(it) }
                form.addButton(Button(data.getString(0), image = image))
                Main.formManager.addForm(form.getName(), form)
                sendButtonList(player, form, listOf("@form.added"))
            }.show(player)
    }

    fun sendAddCommandButton(player: Player, form: ListForm) {
        (CustomForm("@customForm.list.addButton"))
            .setContents(
                mutableListOf(
                    Input("@customForm.text", required = true),
                    Input("@customForm.list.commandButton.command", required = true),
                    Input("@customForm.image", Language.get("form.example", listOf("textures/items/apple")), ""),
                    CancelToggle(fun() { sendButtonList(player, form, listOf("@form.canceled")) }),
                )
            ).onReceive { data ->
                val image = data.getString(2).let { if (it == "") null else ButtonImage(it) }
                form.addButton(CommandButton(data.getString(1), data.getString(0), image))
                Main.formManager.addForm(form.getName(), form)
                sendButtonList(player, form, listOf("@form.added"))
            }.show(player)
    }

    fun sendEditButton(player: Player, form: ListForm, button: Button, index: Int) {
        if (button is CommandButton) {
            sendEditCommandButton(player, form, button, index)
            return
        }

        (CustomForm(button.text))
            .setContents(
                mutableListOf(
                    Label(
                        Language.get("customForm.receive", listOf(index.toString()))
                                + "\n"
                                + Language.get("customForm.receive.list.button", listOf(button.text))
                    ),
                    Input("@customForm.text", default = button.text, required = true),
                    Input(
                        "@customForm.image",
                        Language.get("form.example", listOf("textures/items/apple")),
                        button.image?.image ?: ""
                    ),
                    CancelToggle(null, "@form.delete"),
                )
            ).onReceive { data ->
                val text = data.getString(1)
                val image = data.getString(2)
                val delete = data.getBoolean(3)

                if (delete) {
                    form.removeButton(index)
                } else {
                    button.text = text
                    button.image = if (image == "") null else ButtonImage(image, ButtonImage.TYPE_PATH)
                }
                Main.formManager.addForm(form.getName(), form)
                sendButtonList(player, form, listOf(if (delete) "@form.deleted" else "@form.changed"))
            }.show(player)
    }

    fun sendEditCommandButton(player: Player, form: ListForm, button: CommandButton, index: Int) {
        (CustomForm(button.text))
            .setContents(
                mutableListOf(
                    Label(
                        Language.get("customForm.receive", listOf(index.toString()))
                                + "\n"
                                + Language.get("customForm.receive.list.button", listOf(button.text))
                    ),
                    Input("@customForm.text", default = button.text, required = true),
                    Input("@customForm.list.commandButton.command", default = button.command, required = true),
                    Input(
                        "@customForm.image",
                        Language.get("form.example", listOf("textures/items/apple")),
                        button.image?.image ?: ""
                    ),
                    CancelToggle(null, "@form.delete"),
                )
            ).onReceive { data ->
                val text = data.getString(1)
                val command = data.getString(2)
                val image = data.getString(3)
                val delete = data.getBoolean(4)

                if (delete) {
                    form.removeButton(index)
                } else {
                    button.text = text
                    button.command = command
                    button.image = if (image == "") null else ButtonImage(image, ButtonImage.TYPE_PATH)
                }
                Main.formManager.addForm(form.getName(), form)
                sendButtonList(player, form, listOf(if (delete) "@form.deleted" else "@form.changed"))
            }.show(player)
    }
}