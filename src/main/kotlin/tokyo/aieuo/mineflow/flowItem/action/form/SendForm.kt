package tokyo.aieuo.mineflow.flowItem.action.form

import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.exception.InvalidFlowValueException
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.PlayerFlowItem
import tokyo.aieuo.mineflow.formAPI.CustomForm
import tokyo.aieuo.mineflow.formAPI.ListForm
import tokyo.aieuo.mineflow.formAPI.ModalForm
import tokyo.aieuo.mineflow.formAPI.element.Button
import tokyo.aieuo.mineflow.formAPI.element.Dropdown
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.Input
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.element.mineflow.PlayerVariableDropdown
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.ui.customForm.CustomFormForm
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.ListVariable

class SendForm(player: String = "", var formName: String = "") : FlowItem(), PlayerFlowItem {

    override val id = FlowItemIds.SEND_FORM

    override val nameTranslationKey = "action.sendForm.name"
    override val detailTranslationKey = "action.sendForm.detail"
    override val detailDefaultReplaces = listOf("player", "form")

    override val category = Category.FORM

    override var playerVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setPlayerVariableName(player)
    }

    override fun isDataValid(): Boolean {
        return formName != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getPlayerVariableName(), formName))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val name = source.replaceVariables(formName)
        val manager = Main.formManager
        val helper = Main.variableHelper
        val form = manager.getForm(name)?.clone()
        if (form === null) {
            throw InvalidFlowValueException(Language.get("action.sendForm.notFound", listOf(name)))
        }

        val player = getPlayer(source)
        throwIfInvalidPlayer(player)

        if (form is ModalForm) {
            form.setTitle(source.replaceVariables(form.getTitle()))
            form.setContent(source.replaceVariables(form.getContent()))
            form.setButton1(source.replaceVariables(form.getButton1Text()))
            form.setButton2(source.replaceVariables(form.getButton2Text()))
            form.onReceive { data -> CustomFormForm.onReceive(player, data, form) }
        } else if (form is ListForm) {
            form.setContent(source.replaceVariables(form.getContent()))
            val buttons = mutableListOf<Button>()
            for (button in form.buttons) {
                if (helper.isVariableString(button.text)) {
                    val variableName = button.text.let { it.substring(1, it.length - 1) }
                    val variable = source.getVariable(variableName) ?: helper.getNested(variableName)
                    if (variable is ListVariable) {
                        for (value in variable.value) {
                            buttons.add(Button(value.toString()))
                        }
                        continue
                    }
                }

                buttons.add(button.apply { text = source.replaceVariables(text) })
            }
            form.setButtons(buttons)
            form.onReceive { data -> CustomFormForm.onReceive(player, data, form) }
        } else if (form is CustomForm) {
            val contents = form.contents
            for (content in contents) {
                content.text = source.replaceVariables(content.text)
                if (content is Input) {
                    content.placeholder = source.replaceVariables(content.placeholder)
                    content.default = source.replaceVariables(content.default)
                } else if (content is Dropdown) {
                    val options = mutableListOf<String>()
                    for (option in content.options) {
                        if (helper.isVariableString(option)) {
                            val variableName = option.substring(1, option.length - 1)
                            val variable = source.getVariable(variableName) ?: helper.getNested(variableName)
                            if (variable is ListVariable) {
                                for (value in variable.value) {
                                    options.add(source.replaceVariables(value.toString()))
                                }
                            }
                        } else {
                            options.add(source.replaceVariables(option))
                        }
                    }
                    content.options = options
                }
            }
            form.onReceive { data -> CustomFormForm.onReceive(player, data, form) }
        }
        form.onClose { CustomFormForm.onClose(player, form) }.show(player)
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            PlayerVariableDropdown(variables, getPlayerVariableName()),
            ExampleInput("@action.sendForm.form.name", "aieuo", formName, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setPlayerVariableName(contents.getString(0))
        formName = contents.getString(1)
    }

    override fun serializeContents(): List<Any> {
        return listOf(getPlayerVariableName(), formName)
    }

    override fun clone(): SendForm {
        val item = super.clone() as SendForm
        item.playerVariableNames = playerVariableNames.toMutableMap()
        return item
    }
}