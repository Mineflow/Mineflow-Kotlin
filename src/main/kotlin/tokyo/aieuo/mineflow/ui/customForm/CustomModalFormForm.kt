package tokyo.aieuo.mineflow.ui.customForm

import cn.nukkit.Player
import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.formAPI.CustomForm
import tokyo.aieuo.mineflow.formAPI.ListForm
import tokyo.aieuo.mineflow.formAPI.ModalForm
import tokyo.aieuo.mineflow.formAPI.element.Button
import tokyo.aieuo.mineflow.formAPI.element.CancelToggle
import tokyo.aieuo.mineflow.formAPI.element.Input
import tokyo.aieuo.mineflow.formAPI.element.Label
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.utils.Session
import tokyo.aieuo.mineflow.utils.SimpleCallable

object CustomModalFormForm {
    fun sendMenu(player: Player, form: ModalForm, messages: List<String> = listOf()) {
        (ListForm(Language.get(form.getName())))
            .addButtons(
                Button("@form.back"),
                Button("@form.form.formMenu.preview"),
                Button("@form.recipe.recipeMenu.execute"),
                Button("@form.form.formMenu.changeTitle"),
                Button("@form.form.formMenu.editContent"),
                Button("@form.form.formMenu.modal.button1"),
                Button("@form.form.formMenu.modal.button2"),
                Button("@form.form.formMenu.changeName"),
                Button("@form.form.recipes"),
                Button("@form.delete"),
            ).onReceive { data ->
                when (data) {
                    0 -> {
                        val prev = Session.getSession(player).get<SimpleCallable>("form_menu_prev")
                        if (prev !== null) prev() else CustomFormForm.sendMenu(player)
                    }
                    1 -> {
                        form.onReceive { _ ->
                            sendMenu(player, form)
                        }.onClose {
                            sendMenu(player, form)
                        }.show(player)
                    }
                    2 -> {
                        form.onReceive { data2 ->
                            CustomFormForm.onReceive(player, data2, form)
                        }.onClose {
                            CustomFormForm.onClose(player, form)
                        }.show(player)
                    }
                    3 -> CustomFormForm.sendChangeFormTitle(player, form)
                    4 -> CustomFormForm.sendChangeFormContent(player, form)
                    5 -> sendEditButton(player, form, 1)
                    6 -> sendEditButton(player, form, 2)
                    7 -> CustomFormForm.sendChangeFormName(player, form)
                    8 -> CustomFormForm.sendRecipeList(player, form)
                    9 -> CustomFormForm.sendConfirmDelete(player, form)
                }
            }.addMessages(messages).show(player)
    }

    fun sendEditButton(player: Player, form: ModalForm, index: Int) {
        (CustomForm("@form.form.formMenu.modal.button$index"))
            .setContents(mutableListOf(
                Label(Language.get("customForm.receive", listOf("true"))+"\n"+
                    Language.get("customForm.receive.modal.button", listOf(index.toString()))+"\n"+
                    Language.get("customForm.receive.modal.button.text", listOf(index.toString(), form.getButtonText(index)))),
                Input("@customForm.text", default = form.getButtonText(index)),
                CancelToggle { sendMenu(player, form, listOf("@form.cancelled")) },
            )).onReceive { data ->
                form.setButton(index, data.getString(1))
                Main.formManager.addForm(form.getName(), form)
                sendMenu(player, form, listOf("@form.changed"))
            }.show(player)
    }

}