package tokyo.aieuo.mineflow.flowItem.action.math

import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.NumberVariable

class GenerateRandomNumber(var min: String = "", var max: String = "", resultName: String = "random") :
    TypeGetMathVariable(resultName) {

    override val id = FlowItemIds.GENERATE_RANDOM_NUMBER

    override val nameTranslationKey = "action.generateRandomNumber.name"
    override val detailTranslationKey = "action.generateRandomNumber.detail"
    override val detailDefaultReplaces = listOf("min", "max", "result")

    override fun isDataValid(): Boolean {
        return min != "" && max != "" && resultName != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(min, max, resultName))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        min = source.replaceVariables(min)
        val max = source.replaceVariables(max)
        val resultName = source.replaceVariables(resultName)

        throwIfInvalidNumber(min)
        throwIfInvalidNumber(max)

        val rand = (min.toInt()..max.toInt()).random()
        source.addVariable(resultName, NumberVariable(rand))
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            ExampleInput("@action.generateRandomNumber.form.min", "0", min, true),
            ExampleInput("@action.generateRandomNumber.form.max", "10", max, true),
            ExampleInput("@action.form.resultVariableName", "random", resultName, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        min = contents.getString(0)
        max = contents.getString(1)
        resultName = contents.getString(2)
    }

    override fun serializeContents(): List<Any> {
        return listOf(min, max, resultName)
    }
}