package tokyo.aieuo.mineflow.flowItem.action.math

import tokyo.aieuo.mineflow.exception.InvalidFlowValueException
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.formAPI.element.Dropdown
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleNumberInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.NumberVariable

class FourArithmeticOperations(
    var value1: String = "",
    var operator: Int = ADDITION,
    var value2: String = "",
    var resultName: String = "result"
) : FlowItem() {

    override val id = FlowItemIds.FOUR_ARITHMETIC_OPERATIONS

    override val nameTranslationKey = "action.fourArithmeticOperations.name"
    override val detailTranslationKey = "action.fourArithmeticOperations.detail"
    override val detailDefaultReplaces = listOf("value1", "value2", "operator", "result")

    override val category = Category.MATH

    companion object {
        const val ADDITION = 0
        const val SUBTRACTION = 1
        const val MULTIPLICATION = 2
        const val DIVISION = 3
        const val MODULO = 4
    }

    private val operatorSymbols = listOf("+", "-", "*", "/", "％")

    override fun isDataValid(): Boolean {
        return value1 != "" && value2 != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(
            detailTranslationKey,
            listOf(value1, operatorSymbols.getOrElse(operator) { "?" }, value2, resultName)
        )
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val value1Str = source.replaceVariables(value1)
        val value2Str = source.replaceVariables(value2)
        val resultName = source.replaceVariables(resultName)

        throwIfInvalidNumber(value1Str)
        throwIfInvalidNumber(value2Str)

        val value1 = value1Str.toDouble()
        val value2 = value2Str.toDouble()
        val result = when (operator) {
            ADDITION -> value1 + value2
            SUBTRACTION -> value1 - value2
            MULTIPLICATION -> value1 * value2
            DIVISION -> if (value2 == 0.0) throw InvalidFlowValueException(Language.get("variable.number.div.0")) else value1 / value2
            MODULO -> if (value2 == 0.0) throw InvalidFlowValueException(Language.get("variable.number.div.0")) else value1 % value2
            else -> throw InvalidFlowValueException(
                Language.get(
                    "action.calculate.operator.unknown",
                    listOf(operator.toString())
                )
            )
        }

        source.addVariable(resultName, NumberVariable(result))
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            ExampleNumberInput("@action.fourArithmeticOperations.form.value1", "10", value1, true),
            Dropdown("@action.fourArithmeticOperations.form.operator", operatorSymbols, operator),
            ExampleNumberInput("@action.fourArithmeticOperations.form.value2", "50", value2, true),
            ExampleInput("@action.form.resultVariableName", "result", resultName, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        value1 = contents.getString(0)
        operator = contents.getInt(1)
        value2 = contents.getString(2)
        resultName = contents.getString(3)
    }

    override fun serializeContents(): List<Any> {
        return listOf(value1, operator, value2, resultName)
    }

    override fun getAddingVariables(): DummyVariableMap {
        return mapOf(
            resultName to DummyVariable(DummyVariable.Type.NUMBER)
        )
    }
}