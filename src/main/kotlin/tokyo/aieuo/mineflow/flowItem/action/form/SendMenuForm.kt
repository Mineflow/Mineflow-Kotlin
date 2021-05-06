package tokyo.aieuo.mineflow.flowItem.action.form

import cn.nukkit.Player
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.PlayerFlowItem
import tokyo.aieuo.mineflow.formAPI.ListForm
import tokyo.aieuo.mineflow.formAPI.element.Button
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
import tokyo.aieuo.mineflow.variable.MapVariable
import tokyo.aieuo.mineflow.variable.NumberVariable
import tokyo.aieuo.mineflow.variable.StringVariable

class SendMenuForm(
    player: String = "",
    var formText: String = "",
    var options: List<String> = listOf(),
    var resultName: String = "menu"
) : FlowItem(), PlayerFlowItem {

    override val id = FlowItemIds.SEND_MENU

    override val nameTranslationKey = "action.sendMenu.name"
    override val detailTranslationKey = "action.sendMenu.detail"
    override val detailDefaultReplaces = listOf("player", "text", "options", "result")

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
        return Language.get(
            detailTranslationKey,
            listOf(getPlayerVariableName(), formText, options.joinToString(";"), resultName)
        )
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
        val buttons = mutableListOf<Button>()
        for (option in options) {
            buttons.add(Button(option))
        }

        (ListForm(text))
            .setContent(text)
            .setButtons(buttons)
            .onReceive { data ->
                val variable = MapVariable(
                    mapOf(
                        "id" to NumberVariable(data),
                        "text" to StringVariable(options[data]),
                    ), options[data]
                )
                source.addVariable(resultName, variable)
                source.resume()
            }.onClose {
                if (resendOnClose) sendForm(source, player, text, resultName)
            }.show(player)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        val contents = mutableListOf(
            PlayerVariableDropdown(variables, getPlayerVariableName()),
            ExampleInput("@action.form.resultVariableName", "input", resultName, true),
            ExampleInput("@action.sendInput.form.text", "aieuo", formText, true),
        )
        for ((i, option) in options.withIndex()) {
            contents.add(
                Input(
                    Language.get("customForm.dropdown.option", listOf(i.toString())),
                    Language.get("form.example", listOf("aieuo")),
                    option
                )
            )
        }
        contents.add(ExampleInput("@customForm.dropdown.option.add", "aeiuo"))
        contents.add(Toggle("@action.sendInput.form.resendOnClose", resendOnClose))
        return contents
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun parseFromFormData(_data: CustomFormResponseList): List<Any?> {
        val data = ArrayDeque(_data)
        val target = data.removeFirst()
        val resultName = data.removeFirst()
        val text = data.removeFirst()
        val resendOnClose = data.removeLast()
        val add = (data.removeLast() as String)
            .split(";")
            .map { it.trim() }
            .filterNot { it.isBlank() }

        val options = data.filterNot { (it as String).isBlank() }
        return listOf(target, resultName, text, options + add, resendOnClose)
    }

    @Suppress("UNCHECKED_CAST")
    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setPlayerVariableName(contents.getString(0))
        resultName = contents.getString(1)
        formText = contents.getString(2)
        options = contents[3] as List<String>
        resendOnClose = contents.getBoolean(4)
    }

    override fun serializeContents(): List<Any> {
        return listOf(
            getPlayerVariableName(),
            resultName,
            formText,
            options,
            resendOnClose
        )
    }

    override fun getAddingVariables(): DummyVariableMap {
        return mapOf(
            resultName to DummyVariable(DummyVariable.Type.MAP)
        )
    }

    override fun clone(): SendMenuForm {
        val item = super.clone() as SendMenuForm
        item.playerVariableNames = playerVariableNames.toMutableMap()
        return item
    }
}