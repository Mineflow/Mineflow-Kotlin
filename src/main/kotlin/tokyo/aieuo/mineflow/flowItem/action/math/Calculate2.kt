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
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.NumberVariable
import kotlin.math.*

class Calculate2(var value1: String = "", var value2: String = "", var operator: Int = CALC_MIN, var resultName: String = "result"): FlowItem() {

    override val id = FlowItemIds.CALCULATE2

    override val nameTranslationKey = "action.calculate2.name"
    override val detailTranslationKey = "action.calculate2.detail"
    override val detailDefaultReplaces = listOf("value1", "value2", "operator", "result")

    override val category = Category.MATH
    companion object {
        const val CALC_MIN = 0
        const val CALC_MAX = 1
        const val CALC_POW = 2
        const val CALC_LOG = 3
        const val CALC_HYPOT = 4
        const val CALC_ATAN2 = 5
        const val CALC_ROUND = 6
    }

    private val operatorSymbols = listOf(
        "min(x, y)",
        "max(x, y)",
        "x^y",
        "log_y(x)",
        "√(x^2 + y^2)",
        "atan2(x, y)",
        "round(x, y)"
    )

    override fun isDataValid(): Boolean {
        return value1 != "" && value2 != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(value1, value2, operatorSymbols[operator], resultName))
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
            CALC_MIN -> value1.coerceAtMost(value2)
            CALC_MAX -> value1.coerceAtLeast(value2)
            CALC_POW -> value1.pow(value2)
            CALC_LOG -> log(value1, value2)
            CALC_HYPOT -> hypot(value1, value2)
            CALC_ATAN2 -> atan2(value1, value2)
            CALC_ROUND -> 10.0.pow(value2.toInt()).let { (value1 / it).roundToInt() * it }
            else -> throw InvalidFlowValueException(Language.get("action.calculate.operator.unknown", listOf(operator.toString())))
        }

        source.addVariable(resultName, NumberVariable(result))
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            ExampleNumberInput("@action.calculate2.form.value1", "10", value1, true),
            ExampleNumberInput("@action.calculate2.form.value2", "20", value2, true),
            Dropdown("@action.fourArithmeticOperations.form.operator", operatorSymbols, operator),
            ExampleInput("@action.form.resultVariableName", "result", resultName, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        value1 = contents.getString(0)
        value2 = contents.getString(1)
        operator = contents.getInt(2)
        resultName = contents.getString(3)
    }

    override fun serializeContents(): List<Any> {
        return listOf(value1, value2, operator, resultName)
    }

    override fun getAddingVariables(): Map<String, DummyVariable<DummyVariable.Type>> {
        return mapOf(
            resultName to DummyVariable(DummyVariable.Type.NUMBER)
        )
    }
}