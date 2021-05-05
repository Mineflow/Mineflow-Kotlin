package tokyo.aieuo.mineflow.flowItem.condition

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

class ComparisonString(var value1: String = "", var operator: Int = EQUALS, var value2: String = ""): FlowItem(), Condition {

    override val id = FlowItemIds.COMPARISON_STRING

    override val nameTranslationKey = "condition.comparisonString.name"
    override val detailTranslationKey = "condition.comparisonString.detail"
    override val detailDefaultReplaces = listOf("value1", "operator", "value2")

    override val category = Category.SCRIPT

    companion object {
        const val EQUALS = 0
        const val NOT_EQUALS = 1
        const val CONTAINS = 2
        const val NOT_CONTAINS = 3
        const val STARTS_WITH = 4
        const val ENDS_WITH = 5
    }

    private val operatorSymbols = listOf("==", "!=", "contains", "not contains", "starts with", "ends with")

    override fun isDataValid(): Boolean {
        return value1 != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(
            value1, operatorSymbols[operator],
            value2
        ))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val value1 = source.replaceVariables(value1)
        val value2 = source.replaceVariables(value2)

        val result = when (operator) {
            EQUALS -> value1 == value2
            NOT_EQUALS -> value1 != value2
            CONTAINS -> value2 in value1
            NOT_CONTAINS -> value2 !in value1
            STARTS_WITH -> value1.startsWith(value2)
            ENDS_WITH -> value2.endsWith(value2)
            else -> throw InvalidFlowValueException(Language.get("action.calculate.operator.unknown", listOf(operator.toString())))
        }
        yield(if (result) FlowItemExecutor.Result.SUCCESS else FlowItemExecutor.Result.FAILURE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            ExampleInput("@condition.comparisonNumber.form.value1", "10", value1, true),
            Dropdown("@condition.comparisonNumber.form.operator", operatorSymbols, operator),
            ExampleInput("@condition.comparisonNumber.form.value2", "50", value2, false),
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
