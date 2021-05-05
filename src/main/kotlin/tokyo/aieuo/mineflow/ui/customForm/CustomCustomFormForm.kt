package tokyo.aieuo.mineflow.ui.customForm

import cn.nukkit.Player
import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.formAPI.CustomForm
import tokyo.aieuo.mineflow.formAPI.ListForm
import tokyo.aieuo.mineflow.formAPI.element.*
import tokyo.aieuo.mineflow.utils.*
import java.util.*

object CustomCustomFormForm {

    fun sendMenu(player: Player, form: CustomForm, messages: List<String> = listOf()) {
        (ListForm(Language.get("form.form.formMenu.changeTitle", listOf(form.getName()))))
            .addButton(Button("@form.back"))
            .addButton(Button("@form.form.formMenu.preview"))
            .addButton(Button("@form.recipe.recipeMenu.execute"))
            .addButton(Button("@form.form.formMenu.changeTitle"))
            .addButton(Button("@customForm.custom.element.edit"))
            .addButton(Button("@form.form.formMenu.changeName"))
            .addButton(Button("@form.form.recipes"))
            .addButton(Button("@form.delete"))
            .onReceive { data ->
                when (data) {
                    0 -> {
                        val prev = Session.getSession(player).get<SimpleCallable>("form_menu_prev")
                        if (prev !== null) prev() else (CustomFormForm).sendMenu(player)
                    }
                    1 -> {
                        form.clone().onReceive { _ ->
                            sendMenu(player, form)
                        }.onClose {
                            sendMenu(player, form)
                        }.show(player)
                    }
                    2 -> {
                        form.clone().onReceive { data2 ->
                            CustomFormForm.onReceive(player, data2, form)
                        }.onClose {
                            CustomFormForm.onClose(player, form)
                        }.show(player)
                    }
                    3 -> CustomFormForm.sendChangeFormTitle(player, form)
                    4 -> sendElementList(player, form)
                    5 -> CustomFormForm.sendChangeFormName(player, form)
                    6 -> CustomFormForm.sendRecipeList(player, form)
                    7 -> CustomFormForm.sendConfirmDelete(player, form)
                }
            }.addMessages(messages).show(player)
    }

    fun sendElementList(player: Player, form: CustomForm, messages: List<String> = listOf()) {
        (ListForm("@customForm.custom.element.edit"))
            .addButton(Button("@form.back") { sendMenu(player, form) })
            .addButton(Button("@customForm.custom.element.add") { sendAddElement(player, form) })
            .addButtonsEach(form.contents) { element ->
                Button(
                    Language.get(
                        "customForm.custom.element",
                        listOf(
                            Language.get("customForm.${element.type.typeName}", listOf("")),
                            element.text
                        )
                    )
                ) {
                    sendEditElement(player, form, element)
                }
            }.addMessages(messages).show(player)
    }

    fun sendAddElement(player: Player, form: CustomForm) {
        (CustomForm("@customForm.custom.element.add"))
            .setContents(mutableListOf(
                Dropdown("@customForm.custom.element.select", listOf(
                    Language.get("customForm.label", listOf(" (label)")),
                    Language.get("customForm.input", listOf(" (input)")),
                    Language.get("customForm.numberInput", listOf(" (number input)")),
                    Language.get("customForm.slider", listOf(" (slider)")),
                    Language.get("customForm.step_slider", listOf(" (step_slider)")),
                    Language.get("customForm.dropdown", listOf(" (dropdown)")),
                    Language.get("customForm.toggle", listOf(" (toggle)")),
                    Language.get("customForm.cancelToggle", listOf(" (cancel toggle)")),
                )),
                Input("@customForm.text"),
                CancelToggle { sendElementList(player, form) }
            )).onReceive { data ->
                val text = data.getString(1)
                val element = when (data.getInt(0)) {
                    0 -> Label(text)
                    1 -> Input(text)
                    2 -> NumberInput(text)
                    3 -> Slider(text, 0f, 0f)
                    4 -> StepSlider(text)
                    5 -> Dropdown(text)
                    6 -> Toggle(text)
                    7 -> CancelToggle(null, text)
                    else -> return@onReceive
                }
                form.addContent(element)
                Main.formManager.addForm(form.getName(), form)
                sendEditElement(player, form, element, listOf("@form.added"))
            }.show(player)
    }

    fun sendEditElement(player: Player, form: CustomForm, element: Element, _messages: List<String> = listOf()) {
        val messages = LinkedList(_messages)
        val contents = mutableListOf<Element>(
            Input("@customForm.text", "", element.text)
        )
        val elements = form.contents
        val index = elements.indexOf(element)
        val indexStr = index.toString()
        when (element) {
            is CancelToggle -> {
                messages.push(Language.get("customForm.receive.custom", listOf(indexStr, "(true | false)")))
                messages.push(Language.get("customForm.cancelToggle.detail"))
                contents.add(Toggle("@customForm.default", element.default))
            }
            is Toggle -> {
                messages.push(Language.get("customForm.receive.custom", listOf(indexStr, "(true | false)")))
                contents.add(Toggle("@customForm.default", element.default))
            }
            is Label -> {
                messages.push(Language.get("customForm.receive.custom", listOf(indexStr, "")))
            }
            is NumberInput -> {
                messages.push(Language.get("customForm.receive.custom.input", listOf(indexStr)))
                contents.add(Input("@customForm.input.placeholder", default = element.placeholder))
                contents.add(NumberInput("@customForm.default", default = element.default))
                contents.add(Toggle("@customForm.input.required", element.required))
                contents.add(NumberInput("@customForm.numberInput.min", default = element.min?.toString() ?: ""))
                contents.add(NumberInput("@customForm.numberInput.max", default = element.max?.toString() ?: ""))
            }
            is Input -> {
                messages.push(Language.get("customForm.receive.custom.input", listOf(indexStr)))
                contents.add(Input("@customForm.input.placeholder", default = element.placeholder))
                contents.add(Input("@customForm.default", default = element.default))
                contents.add(Toggle("@customForm.input.required", element.required))
            }
            is Slider -> {
                messages.push(Language.get("customForm.receive.custom.slider", listOf(indexStr)))
                contents.add(NumberInput("@customForm.slider.min", default = element.min.toString()))
                contents.add(NumberInput("@customForm.slider.max", default = element.max.toString()))
                contents.add(NumberInput("@customForm.slider.step", default = element.step.toString()))
                contents.add(NumberInput("@customForm.default", default = element.default.toString()))
            }
            is Dropdown -> {
                val dropdown = elements.filterIsInstance<Dropdown>().indexOf(element)
                messages.push(Language.get("customForm.receive.custom.dropdown.text", listOf(dropdown.toString())))
                messages.push(Language.get("customForm.receive.custom.dropdown", listOf(indexStr)))
                for ((i, option) in element.options.withIndex()) {
                    contents.add(
                        Input(Language.get("customForm.dropdown.option", listOf(i.toString())), default = option)
                    )
                }
                contents.add(Input("@customForm.dropdown.option.add"))
            }
        }
        contents.add(Toggle("@form.delete"))

        (CustomForm("@customForm.custom.element.edit"))
            .setContents(contents)
            .onReceive { data ->
                if (data.last() as Boolean) {
                    form.removeContentAt(index)
                    Main.formManager.addForm(form.getName(), form)
                    sendElementList(player, form, listOf("@form.deleted"))
                    return@onReceive
                }
                element.text = data.first() as String

                when (element) {
                    is Toggle -> {
                        element.default = data.getBoolean(1)
                    }
                    is NumberInput -> {
                        element.placeholder = data.getString(1)
                        element.default = data.getString(2)
                        element.required = data.getBoolean(3)
                        element.min = data.getDoubleOrNull(4)
                        element.max = data.getDoubleOrNull(5)
                    }
                    is Input -> {
                        element.placeholder = data.getString(1)
                        element.default = data.getString(2)
                        element.required = data.getBoolean(3)
                    }
                    is Slider -> {
                        element.min = data.getFloatOrNull(1) ?: 0f
                        element.max = data.getFloatOrNull(2) ?: 0f
                        element.step = data.getFloatOrNull(3) ?: 0f
                        element.default = data.getFloatOrNull(4) ?: 0f
                    }
                    is Dropdown -> {
                        val add = mutableListOf<String>()
                        val options = mutableListOf<String>()
                        for (item in data.getString(data.lastIndex - 1).split(";")) {
                            if (item != "") add.add(item)
                        }
                        for (option in data.subList(0, data.lastIndex)) {
                            if (option != "") options.add(option as String)
                        }
                        element.options = options + add
                    }
                }

                form.setContent(element, index)
                Main.formManager.addForm(form.getName(), form)
                sendElementList(player, form, listOf("@form.changed"))
            }.addMessages(messages).show(player)
    }
}