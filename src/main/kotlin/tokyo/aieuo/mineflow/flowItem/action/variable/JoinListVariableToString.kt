package tokyo.aieuo.mineflow.flowItem.action.variable

import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.exception.InvalidFlowValueException
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.ListVariable
import tokyo.aieuo.mineflow.variable.StringVariable

class JoinListVariableToString(
    var variableName: String = "",
    var separator: String = "",
    var resultName: String = "result"
) : FlowItem() {

    override val id = FlowItemIds.JOIN_LIST_VARIABLE_TO_STRING

    override val nameTranslationKey = "action.joinToString.name"
    override val detailTranslationKey = "action.joinToString.detail"
    override val detailDefaultReplaces = listOf("name", "separator", "result")

    override val category = Category.VARIABLE

    override fun isDataValid(): Boolean {
        return separator != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(variableName, separator, resultName))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val helper = Main.variableHelper
        val name = source.replaceVariables(variableName)
        val separator = source.replaceVariables(separator)
        val result = source.replaceVariables(resultName)

        val variable = source.getVariable(name) ?: helper.getNested(name)
        if (variable === null) {
            throw InvalidFlowValueException(Language.get("variable.notFound", listOf(name)))
        }
        if (variable !is ListVariable) {
            throw InvalidFlowValueException(
                Language.get(
                    "action.addListVariable.error.existsOtherType",
                    listOf(name, variable.toString())
                )
            )
        }

        val strings = mutableListOf<String>()
        variable.value.forEach {
            strings.add(it.toString())
        }
        source.addVariable(result, StringVariable(strings.joinToString(separator)))
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            ExampleInput("@action.variable.form.name", "aieuo", variableName, true),
            ExampleInput("@action.joinToString.form.separator", ", ", separator, false),
            ExampleInput("@action.form.resultVariableName", "string", resultName, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        variableName = contents.getString(0)
        separator = contents.getString(1)
        resultName = contents.getString(2)
    }

    override fun serializeContents(): List<Any> {
        return listOf(variableName, separator, resultName)
    }

    override fun getAddingVariables(): DummyVariableMap {
        return mapOf(
            resultName to DummyVariable(DummyVariable.Type.STRING)
        )
    }
}