package tokyo.aieuo.mineflow.flowItem.action.form

import cn.nukkit.Player
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.PlayerFlowItem
import tokyo.aieuo.mineflow.formAPI.CustomForm
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.Input
import tokyo.aieuo.mineflow.formAPI.element.Toggle
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.element.mineflow.PlayerVariableDropdown
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.StringVariable

class SendInputForm(player: String = "", var formText: String = "", var resultName: String = "input") : FlowItem(),
    PlayerFlowItem {

    override val id = FlowItemIds.SEND_INPUT

    override val nameTranslationKey = "action.sendInput.name"
    override val detailTranslationKey = "action.sendInput.detail"
    override val detailDefaultReplaces = listOf("player", "text", "result")

    override val category = Category.FORM

    override var playerVariableNames: MutableMap<String, String> = mutableMapOf()

    var resendOnClose = false

    init {
        setPlayerVariableName(player)
    }

    override fun isDataValid(): Boolean {
        return getPlayerVariableName() != "" && formText != "" && resultName != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getPlayerVariableName(), formText, resultName))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val text = source.replaceVariables(formText)
        val resultName = source.replaceVariables(resultName)

        val player = getPlayer(source)
        throwIfInvalidPlayer(player)

        sendForm(source, player, text, resultName)
        yield(FlowItemExecutor.Result.AWAIT)
    }

    private fun sendForm(source: FlowItemExecutor, player: Player, text: String, resultName: String) {
        (CustomForm(text))
            .setContents(
                mutableListOf(
                    Input(text, "", "", true),
                )
            ).onReceive { data ->
                val variable = StringVariable(data.getString(0))
                source.addVariable(resultName, variable)
                source.resume()
            }.onClose {
                if (resendOnClose) sendForm(source, player, text, resultName)
            }.show(player)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            PlayerVariableDropdown(variables, getPlayerVariableName()),
            ExampleInput("@action.form.resultVariableName", "input", resultName, true),
            ExampleInput("@action.sendInput.form.text", "aieuo", formText, true), // TODO: placeholder, default
            Toggle("@action.sendInput.form.resendOnClose", resendOnClose),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setPlayerVariableName(contents.getString(0))
        resultName = contents.getString(1)
        formText = contents.getString(2)
        resendOnClose = contents.getBoolean(3)
    }

    override fun serializeContents(): List<Any> {
        return listOf(getPlayerVariableName(), resultName, formText, resendOnClose)
    }

    override fun getAddingVariables(): DummyVariableMap {
        return mapOf(
            resultName to DummyVariable(DummyVariable.Type.STRING)
        )
    }

    override fun clone(): SendInputForm {
        val item = super.clone() as SendInputForm
        item.playerVariableNames = playerVariableNames.toMutableMap()
        return item
    }
}