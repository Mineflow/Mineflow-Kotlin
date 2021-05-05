package tokyo.aieuo.mineflow.flowItem.action.string

import tokyo.aieuo.mineflow.exception.InvalidFlowValueException
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.formAPI.element.Dropdown
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.ListVariable
import tokyo.aieuo.mineflow.variable.StringVariable

class EditString(var value1: String = "", var operator: String = TYPE_JOIN, var value2: String = "", var resultName: String = "result"): FlowItem() {

    override val id = FlowItemIds.EDIT_STRING

    override val nameTranslationKey = "action.editString.name"
    override val detailTranslationKey = "action.editString.detail"
    override val detailDefaultReplaces = listOf("value1", "operator", "value2", "result")

    override val category = Category.STRING

    companion object {
        const val TYPE_JOIN = "join"
        const val TYPE_DELETE = "delete"
        const val TYPE_REPEAT = "repeat"
        const val TYPE_SPLIT = "split"
    }


    private val operators = listOf(
        TYPE_JOIN,
        TYPE_DELETE,
        TYPE_REPEAT,
        TYPE_SPLIT,
    )

    override fun isDataValid(): Boolean {
        return value1 != "" && value2 != "" && operator != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(value1, Language.get("action.editString.${operator}"), value2, resultName))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val value1 = source.replaceVariables(value1)
        val value2 = source.replaceVariables(value2)
        val resultName = source.replaceVariables(resultName)

        val result = when (operator) {
            TYPE_JOIN -> StringVariable(value1 + value2)
            TYPE_DELETE -> StringVariable(value1.replace(value2, ""))
            TYPE_REPEAT -> {
                throwIfInvalidNumber(value2, 1.0)
                StringVariable(value1.repeat(value2.toInt()))
            }
            TYPE_SPLIT -> {
                ListVariable(value1.split(value2).map { StringVariable(it) })
            }
            else -> throw InvalidFlowValueException(Language.get("action.calculate.operator.unknown", listOf(operator)))
        }

        source.addVariable(resultName, result)
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            ExampleInput("@action.fourArithmeticOperations.form.value1", "10", value1, true),
            Dropdown("@action.fourArithmeticOperations.form.operator",
                operators.map { Language.get("action.editString.$it") },
                operators.indexOf(operator)
            ),
            ExampleInput("@action.fourArithmeticOperations.form.value2", "50", value2, true),
            ExampleInput("@action.form.resultVariableName", "result", resultName, true),
        )
    }

    override fun parseFromFormData(data: CustomFormResponseList): List<Any?> {
        return listOf(data[0], operators[data.getInt(1)], data[2], data[3])
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        value1 = contents.getString(0)
        operator = contents.getString(1)
        value2 = contents.getString(2)
        resultName = contents.getString(3)
    }

    override fun serializeContents(): List<Any> {
        return listOf(value1, operator, value2, resultName)
    }

    override fun getAddingVariables(): Map<String, DummyVariable<DummyVariable.Type>> {
        return mapOf(
            resultName to DummyVariable(DummyVariable.Type.STRING, "$value1 (${operator}) $value2")
        )
    }
}