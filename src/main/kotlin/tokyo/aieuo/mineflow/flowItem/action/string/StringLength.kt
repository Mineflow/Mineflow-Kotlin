package tokyo.aieuo.mineflow.flowItem.action.string

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
import tokyo.aieuo.mineflow.variable.NumberVariable

class StringLength(var value: String = "", var resultName: String = "length") : FlowItem() {

    override val id = FlowItemIds.STRING_LENGTH

    override val nameTranslationKey = "action.strlen.name"
    override val detailTranslationKey = "action.strlen.detail"
    override val detailDefaultReplaces = listOf("string", "result")

    override val category = Category.STRING

    override fun isDataValid(): Boolean {
        return value != "" && resultName != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(value, resultName))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val value = source.replaceVariables(value)
        val resultName = source.replaceVariables(resultName)

        val length = value.length
        source.addVariable(resultName, NumberVariable(length))
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            ExampleInput("@action.strlen.form.value", "aieuo", value, true),
            ExampleInput("@action.form.resultVariableName", "length", resultName, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        value = contents.getString(0)
        resultName = contents.getString(1)
    }

    override fun serializeContents(): List<Any> {
        return listOf(value, resultName)
    }

    override fun getAddingVariables(): DummyVariableMap {
        return mapOf(
            resultName to DummyVariable(DummyVariable.Type.NUMBER, value)
        )
    }
}