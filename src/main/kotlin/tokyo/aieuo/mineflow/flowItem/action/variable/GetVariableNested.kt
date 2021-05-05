package tokyo.aieuo.mineflow.flowItem.action.variable

import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.exception.InvalidFlowValueException
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable

class GetVariableNested(var variableName: String = "", var resultName: String = "var"): FlowItem() {

    override val id = FlowItemIds.GET_VARIABLE_NESTED

    override val nameTranslationKey = "action.getVariableNested.name"
    override val detailTranslationKey = "action.getVariableNested.detail"
    override val detailDefaultReplaces = listOf("name", "result")

    override val category = Category.VARIABLE

    override fun isDataValid(): Boolean {
        return variableName != "" && resultName != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(variableName, resultName))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val variableName = source.replaceVariables(variableName)
        val resultName = source.replaceVariables(resultName)

        val variable = source.getVariable(variableName) ?: Main.variableHelper.getNested(variableName)
        if (variable === null) {
            throw InvalidFlowValueException(Language.get("variable.notFound", listOf(variableName)))
        }

        source.addVariable(resultName, variable)
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            ExampleInput("@action.getVariableNested.form.target", "target.hand", variableName, true),
            ExampleInput("@action.form.resultVariableName", "item", resultName, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        variableName = contents.getString(0)
        resultName = contents.getString(1)
    }

    override fun serializeContents(): List<Any> {
        return listOf(variableName, resultName)
    }

    override fun getAddingVariables(): Map<String, DummyVariable<DummyVariable.Type>> {
        return mapOf(
            resultName to DummyVariable(DummyVariable.Type.UNKNOWN)
        )
    }
}