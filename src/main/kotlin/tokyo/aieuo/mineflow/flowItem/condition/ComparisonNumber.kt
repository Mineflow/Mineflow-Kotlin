package tokyo.aieuo.mineflow.flowItem.condition

import tokyo.aieuo.mineflow.exception.InvalidFlowValueException
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.formAPI.element.Dropdown
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleNumberInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language

class ComparisonNumber(var value1: String = "", var operator: Int = EQUAL, var value2: String = "") :
    FlowItem(), Condition {

    override val id = FlowItemIds.COMPARISON_NUMBER

    override val nameTranslationKey = "condition.comparisonNumber.name"
    override val detailTranslationKey = "condition.comparisonNumber.detail"
    override val detailDefaultReplaces = listOf("value1", "operator", "value2")

    override val category = Category.SCRIPT

    companion object {
        const val EQUAL = 0
        const val NOT_EQUAL = 1
        const val GREATER = 2
        const val LESS = 3
        const val GREATER_EQUAL = 4
        const val LESS_EQUAL = 5
    }

    private val operatorSymbols = listOf("==", "!=", ">", "<", ">=", "<=")

    override fun isDataValid(): Boolean {
        return value1 != "" && value2 != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(value1, operatorSymbols[operator], value2))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val value1Str = source.replaceVariables(value1)
        val value2Str = source.replaceVariables(value2)

        throwIfInvalidNumber(value1Str)
        throwIfInvalidNumber(value2Str)

        val value1 = value1Str.toDouble()
        val value2 = value2Str.toDouble()
        val result = when (operator) {
            EQUAL -> value1 == value2
            NOT_EQUAL -> value1 != value2
            GREATER -> value1 > value2
            LESS -> value1 < value2
            GREATER_EQUAL -> value1 >= value2
            LESS_EQUAL -> value1 <= value2
            else -> throw InvalidFlowValueException(
                Language.get("action.calculate.operator.unknown", listOf(operator.toString()))
            )
        }
        yield(if (result) FlowItemExecutor.Result.SUCCESS else FlowItemExecutor.Result.FAILURE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            ExampleNumberInput("@condition.comparisonNumber.form.value1", "10", value1, true),
            Dropdown("@condition.comparisonNumber.form.operator", operatorSymbols, operator),
            ExampleNumberInput("@condition.comparisonNumber.form.value2", "50", value2, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        value1 = contents.getString(0)
        operator = contents.getInt(1)
        value2 = contents.getString(2)
    }

    override fun serializeContents(): List<Any> {
        return listOf(value1, operator, value2)
    }
}
