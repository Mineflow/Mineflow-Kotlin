package tokyo.aieuo.mineflow.flowItem.action.math

import tokyo.aieuo.mineflow.exception.InvalidFlowValueException
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.formAPI.element.Dropdown
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.NumberVariable
import kotlin.math.*

class Calculate(var value: String = "", var operator: Int = SQUARE, var resultName: String = "result") : FlowItem() {

    override val id = FlowItemIds.CALCULATE

    override val nameTranslationKey = "action.calculate.name"
    override val detailTranslationKey = "action.calculate.detail"
    override val detailDefaultReplaces = listOf("value", "operator", "result")

    override val category = Category.MATH

    companion object {
        const val SQUARE = 0
        const val SQUARE_ROOT = 1
        const val FACTORIAL = 2
        const val CALC_ABS = 3
        const val CALC_LOG = 4
        const val CALC_SIN = 5
        const val CALC_COS = 6
        const val CALC_TAN = 7
        const val CALC_ASIN = 8
        const val CALC_ACOS = 9
        const val CALC_ATAN = 10
        const val CALC_DEG2RAD = 11
        const val CALC_RAD2DEG = 12
        const val CALC_FLOOR = 13
        const val CALC_ROUND = 14
        const val CALC_CEIL = 15
    }

    private val operatorSymbols = listOf(
        "x^2",
        "√x",
        "x!",
        "abs(x)",
        "log(x)",
        "sin(x)",
        "cos(x)",
        "tan(x)",
        "asin(x)",
        "acos(x)",
        "atan(x)",
        "deg2rad(x)",
        "rad2deg(x)",
        "floor(x)",
        "round(x)",
        "ceil(x)"
    )

    override fun isDataValid(): Boolean {
        return value != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(value, operatorSymbols[operator], resultName))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val valueStr = source.replaceVariables(value)
        val resultName = source.replaceVariables(resultName)

        throwIfInvalidNumber(valueStr)

        val value = valueStr.toDouble()
        val result = when (operator) {
            SQUARE -> value * value
            SQUARE_ROOT -> sqrt(value)
            FACTORIAL -> {
                var r = 1
                for (i in abs(value).toInt() downTo 2) {
                    r *= i
                }
                r
            }
            CALC_ABS -> abs(value)
            CALC_LOG -> log10(value)
            CALC_SIN -> sin(value)
            CALC_COS -> cos(value)
            CALC_TAN -> tan(value)
            CALC_ASIN -> asin(value)
            CALC_ACOS -> acos(value)
            CALC_ATAN -> atan(value)
            CALC_DEG2RAD -> value * (Math.PI / 180)
            CALC_RAD2DEG -> value * (180 / Math.PI)
            CALC_FLOOR -> floor(value)
            CALC_ROUND -> round(value)
            CALC_CEIL -> ceil(value)
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
            ExampleInput("@action.calculate.form.value", "10", value, true),
            Dropdown("@action.fourArithmeticOperations.form.operator", operatorSymbols, operator),
            ExampleInput("@action.form.resultVariableName", "result", resultName, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        value = contents.getString(0)
        operator = contents.getInt(1)
        resultName = contents.getString(2)
    }

    override fun serializeContents(): List<Any> {
        return listOf(value, operator, resultName)
    }

    override fun getAddingVariables(): DummyVariableMap {
        return mapOf(
            resultName to DummyVariable(DummyVariable.Type.NUMBER)
        )
    }
}