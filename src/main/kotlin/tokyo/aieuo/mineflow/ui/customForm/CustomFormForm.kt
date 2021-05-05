package tokyo.aieuo.mineflow.ui.customForm

import cn.nukkit.Player
import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.exception.InvalidFormValueException
import tokyo.aieuo.mineflow.formAPI.*
import tokyo.aieuo.mineflow.formAPI.element.Button
import tokyo.aieuo.mineflow.formAPI.element.CancelToggle
import tokyo.aieuo.mineflow.formAPI.element.Dropdown
import tokyo.aieuo.mineflow.formAPI.element.Input
import tokyo.aieuo.mineflow.trigger.TriggerHolder
import tokyo.aieuo.mineflow.trigger.form.FormTrigger
import tokyo.aieuo.mineflow.ui.HomeForm
import tokyo.aieuo.mineflow.ui.MineflowForm
import tokyo.aieuo.mineflow.ui.RecipeForm
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.utils.Session

object CustomFormForm {

    fun sendMenu(player: Player, messages: List<String> = listOf()) {
        (ListForm("@form.form.menu.title"))
            .addButtons(
                Button("@form.back") { HomeForm.sendMenu(player) },
                Button("@form.add") { sendAddForm(player) },
                Button("@form.edit") { sendSelectForm(player) },
                Button("@form.form.menu.formList") { sendFormList(player) },
            ).addMessages(messages).show(player)
    }

    fun sendAddForm(player: Player, defaultName: String = "") {
        (CustomForm("@form.form.addForm.title"))
            .setContents(mutableListOf(
                Input("@customForm.name", "", defaultName, true),
                Dropdown("@form.form.addForm.type", listOf(
                    Language.get("customForm.modal"),
                    Language.get("customForm.form"),
                    Language.get("customForm.custom_form"),
                )),
                CancelToggle { sendMenu(player) },
            )).onReceive { data ->
                val name = data.getString(0)
                val form = when (data.getInt(1)) {
                    0 -> ModalForm(name)
                    1 -> ListForm(name)
                    2 -> CustomForm(name)
                    else -> throw InvalidFormValueException("@form.insufficient", 1)
                }

                val manager = Main.formManager
                if (manager.existsForm(name)) {
                    val newName = manager.getNotDuplicatedName(name)
                    MineflowForm.confirmRename(player, name, newName, {
                        form.setTitle(it)
                        manager.addForm(it, form)
                        Session.getSession(player).set("form_menu_prev") {
                            sendMenu(player)
                        }
                        sendFormMenu(player, form)
                    }, {
                        resend(Language.get("form.form.exists", listOf(it)) to 0)
                    })
                    return@onReceive
                }
                manager.addForm(name, form)
                Session.getSession(player).set("form_menu_prev") {
                    sendMenu(player)
                }
                sendFormMenu(player, form)
            }.show(player)
    }

    fun sendSelectForm(player: Player) {
        (CustomForm("@form.form.select.title"))
            .setContents(mutableListOf(
                Input("@customForm.name", required = true),
                CancelToggle { sendMenu(player) },
            )).onReceive { data ->
                val manager = Main.formManager
                val name = data.getString(0)
                val form = manager.getForm(name)

                if (form === null) {
                    resend("@form.form.notFound" to 0)
                    return@onReceive
                }

                Session.getSession(player).set("form_menu_prev") {
                    sendSelectForm(player)
                }
                sendFormMenu(player, form)
            }.show(player)
    }

    fun sendFormList(player: Player) {
        val manager = Main.formManager
        val forms = manager.all.values.toList()
        val buttons = mutableListOf(
            Button("@form.back") { sendMenu(player) }
        )
        for (form in forms) {
            buttons.add(Button("${form.getName()}: ${Language.get("customForm.${form.type}")}"))
        }

        (ListForm("@form.form.menu.formList"))
            .addButtons(buttons)
            .onReceive { data ->
                val form = forms[data - 1]
                Session.getSession(player).set("form_menu_prev") {
                    sendFormList(player)
                }
                sendFormMenu(player, form)
            }.show(player)
    }

    fun sendFormMenu(player: Player, form: Form, messages: List<String> = listOf()) {
        when (true) {
            form is ModalForm -> CustomModalFormForm.sendMenu(player, form, messages)
            form is ListForm -> CustomListFormForm.sendMenu(player, form, messages)
            form is CustomForm -> CustomCustomFormForm.sendMenu(player, form, messages)
        }
    }

    fun sendChangeFormTitle(player: Player, form: Form) {
        (CustomForm("@form.form.formMenu.changeTitle"))
            .setContents(mutableListOf(
                Input("@customForm.title", default = form.getTitle()),
                CancelToggle(fun() { sendFormMenu(player, form, listOf("@form.cancelled")) }),
            )).onReceive { data ->
                form.setTitle(data.getString(0))
                Main.formManager.addForm(form.getName(), form)
                sendFormMenu(player, form, listOf("@form.changed"))
            }.show(player)
    }

    fun sendChangeFormContent(player: Player, form: ModalForm) {
        (CustomForm("@form.form.formMenu.editContent"))
            .setContents(mutableListOf(
                Input("@customForm.content", default = form.getContent()),
                CancelToggle { sendFormMenu(player, form, listOf("@form.cancelled")) },
            )).onReceive { data ->
                form.setContent(data.getString(0))
                Main.formManager.addForm(form.getName(), form)
                sendFormMenu(player, form, listOf("@form.changed"))
            }.show(player)
    }

    fun sendChangeFormContent(player: Player, form: ListForm) {
        (CustomForm("@form.form.formMenu.editContent"))
            .setContents(mutableListOf(
                Input("@customForm.content", default = form.getContent()),
                CancelToggle { sendFormMenu(player, form, listOf("@form.cancelled")) },
            )).onReceive { data ->
                form.setContent(data.getString(0))
                Main.formManager.addForm(form.getName(), form)
                sendFormMenu(player, form, listOf("@form.changed"))
            }.show(player)
    }

    fun sendChangeFormName(player: Player, form: Form) {
        (CustomForm("@form.form.formMenu.changeName"))
            .setContents(mutableListOf(
                Input("@customForm.name", required = true),
                CancelToggle { sendFormMenu(player, form, listOf("@form.cancelled")) },
            )).onReceive { data ->
                val name = data.getString(0)
                val manager = Main.formManager
                if (manager.existsForm(name)) {
                    val newName = manager.getNotDuplicatedName(name)
                    MineflowForm.confirmRename(player, name, newName, {
                        manager.removeForm(form.getName())
                        form.setName(it)
                        manager.addForm(it, form)
                        sendFormMenu(player, form, listOf("@form.changed"))
                    }, {
                        resend(Language.get("customForm.exists", listOf(it)) to 0)
                    })
                    return@onReceive
                }

                manager.removeForm(form.getName())
                form.setName(name)
                manager.addForm(name, form)
                sendFormMenu(player, form, listOf("@form.changed"))
            }.show(player)
    }

    fun sendRecipeList(player: Player, form: Form, messages: List<String> = listOf()) {
        val recipes = Main.formManager.getAssignedRecipes(form.getName())
        (ListForm(Language.get("form.recipes.title", listOf(form.getName()))))
            .addButton(Button("@form.back") { sendFormMenu(player, form) })
            .addButton(Button("@form.add") { sendSelectRecipe(player, form) })
            .addButtonsEach(recipes) { keys, name ->
                Button("$name | ${keys.size}") {
                    sendRecipeMenu(player, form, name, keys)
                }
            }.addMessages(messages).show(player)
    }

    fun sendSelectRecipe(player: Player, form: Form) {
        (CustomForm(Language.get("form.recipes.add", listOf(form.getName()))))
            .setContents(mutableListOf(
                Input("@form.recipe.recipeName", required = true),
                CancelToggle { sendRecipeList(player, form) },
            )).onReceive { data ->
                val manager = Main.recipeManager
                val (name, group) = manager.parseName(data.getString(0))
                val recipe = manager.get(name, group)
                if (recipe === null) {
                    resend("@form.recipe.select.notfound" to 0)
                    return@onReceive
                }

                val trigger = FormTrigger.create(form.getName())
                if (recipe.existsTrigger(trigger)) {
                    sendRecipeList(player, form, listOf("@trigger.alreadyExists"))
                    return@onReceive
                }
                recipe.addTrigger(trigger)
                sendRecipeList(player, form, listOf("@form.added"))
            }.show(player)
    }

    fun sendRecipeMenu(player: Player, form: Form, name: String, triggers: List<String>) {
        val content = triggers.joinToString("\n") {
            when (it) {
                "" -> Language.get("trigger.form.receive")
                "close" -> Language.get("trigger.form.close")
                else -> if (form is ListForm) {
                    val button = form.getButtonById(it)
                    Language.get(
                        "trigger.form.button",
                        listOf(if (button is Button) button.text else "")
                    )
                } else {
                    ""
                }
            }
        }

        (ListForm(Language.get("form.recipes.title", listOf(form.getName()))))
            .setContent(content)
            .setButtons(mutableListOf(
                Button("@form.back") { sendRecipeList(player, form) },
                Button("@form.edit") {
                    Session.getSession(player).set("recipe_menu_prev", fun() {
                        sendRecipeMenu(player, form, name, triggers)
                    })
                    val (name1, group) = Main.recipeManager.parseName(name)
                    val recipe = Main.recipeManager.get(name1, group) ?: return@Button
                    RecipeForm.sendTriggerList(player, recipe)
                }
            )).show(player)
    }

    fun sendConfirmDelete(player: Player, form: Form) {
        (ModalForm(Language.get("form.recipe.delete.title", listOf(form.getName()))))
            .setContent(Language.get("form.delete.confirm", listOf(form.getName())))
            .onYes {
                Main.formManager.removeForm(form.getName())
                sendMenu(player, listOf("@form.deleted"))
            }.onNo {
                sendFormMenu(player, form, listOf("@form.cancelled"))
            }.show(player)
    }

    fun onReceive(player: Player, data: Boolean, form: ModalForm) {
        val trigger = FormTrigger.create(form.getName())
        val variables = trigger.getVariables(form, data)
        if (TriggerHolder.existsRecipe(trigger)) {
            val recipes = TriggerHolder.getRecipes(trigger)
            recipes?.executeAll(player, variables)
        }

        trigger.subKey = if (data) "1" else "2"
        if (TriggerHolder.existsRecipe(trigger)) {
            val recipes = TriggerHolder.getRecipes(trigger)
            recipes?.executeAll(player, variables)
        }
        form.resetErrors()
    }

    fun onReceive(player: Player, data: Int, form: ListForm) {
        val trigger = FormTrigger.create(form.getName())
        val variables = trigger.getVariables(form, data)
        if (TriggerHolder.existsRecipe(trigger)) {
            val recipes = TriggerHolder.getRecipes(trigger)
            recipes?.executeAll(player, variables)
        }

        form.getButton(data)?.let { button ->
            trigger.subKey = button.getUUID()
            if (TriggerHolder.existsRecipe(trigger)) {
                val recipes = TriggerHolder.getRecipes(trigger)
                recipes?.executeAll(player, variables)
            }
        }
        form.resetErrors()
    }

    fun onReceive(player: Player, data: List<*>, form: CustomForm) {
        val trigger = FormTrigger.create(form.getName())
        val variables = trigger.getVariables(form, data)
        if (TriggerHolder.existsRecipe(trigger)) {
            val recipes = TriggerHolder.getRecipes(trigger)
            recipes?.executeAll(player, variables)
        }
        form.resetErrors()
    }

    fun onClose(player: Player, form: Form) {
        val trigger = FormTrigger.create(form.getName(), "close")
        if (TriggerHolder.existsRecipe(trigger)) {
            val recipes = TriggerHolder.getRecipes(trigger)
            recipes?.executeAll(player)
        }
        form.resetErrors()
    }
}
