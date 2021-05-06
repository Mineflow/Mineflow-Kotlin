package tokyo.aieuo.mineflow.flowItem.action.math

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
import tokyo.aieuo.mineflow.utils.is_numeric
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.NumberVariable

class CalculateReversePolishNotation(var formula: String = "", var resultName: String = "result") : FlowItem() {

    override val id = FlowItemIds.REVERSE_POLISH_NOTATION

    override val nameTranslationKey = "action.calculateRPN.name"
    override val detailTranslationKey = "action.calculateRPN.detail"
    override val detailDefaultReplaces = listOf("formula", "result")

    override val category = Category.MATH

    override fun isDataValid(): Boolean {
        return formula != "" && resultName != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(formula, resultName))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val formula = source.replaceVariables(formula)
        val resultName = source.replaceVariables(resultName)

        val stack = ArrayDeque<Double>()
        for (token in formula.split(" ")) {
            if (is_numeric(token)) {
                stack.addLast(token.toDouble())
                continue
            }

            val value2 = stack.removeLast()
            val value1 = stack.removeLast()
            val res = when (token) {
                "+" -> value1 + value2
                "-" -> value1 - value2
                "*" -> value1 * value2
                "/" -> value1 / value2
                "%" -> value1 % value2
                else -> throw InvalidFlowValueException(
                    Language.get(
                        "action.calculate.operator.unknown",
                        listOf(token)
                    )
                )
            }
            stack.addLast(res)
        }
        val result = stack.first()

        source.addVariable(resultName, NumberVariable(result))
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            ExampleInput("@action.calculateRPN.form.value", "1 2 + 3 -", formula, true),
            ExampleInput("@action.form.resultVariableName", "result", resultName, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        formula = contents.getString(0)
        resultName = contents.getString(1)
    }

    override fun serializeContents(): List<Any> {
        return listOf(formula, resultName)
    }

    override fun getAddingVariables(): DummyVariableMap {
        return mapOf(
            resultName to DummyVariable(DummyVariable.Type.NUMBER)
        )
    }
}