package tokyo.aieuo.mineflow.flowItem.condition

import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable

class ExistsVariable(var variableName: String = ""): FlowItem(), Condition {

    override val id = FlowItemIds.EXISTS_VARIABLE

    override val nameTranslationKey = "condition.existsVariable.name"
    override val detailTranslationKey = "condition.existsVariable.detail"
    override val detailDefaultReplaces = listOf("name")

    override val category = Category.VARIABLE

    override fun isDataValid(): Boolean {
        return variableName != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(variableName))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val helper = Main.variableHelper
        val name = source.replaceVariables(variableName)

        val result = source.getVariable(name) !== null || helper.get(name) !== null || helper.getNested(name) !== null
        yield(if (result) FlowItemExecutor.Result.SUCCESS else FlowItemExecutor.Result.FAILURE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            ExampleInput("@action.variable.form.name", "aieuo", variableName, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        variableName = contents.getString(0)
    }

    override fun serializeContents(): List<Any> {
        return listOf(variableName)
    }
}