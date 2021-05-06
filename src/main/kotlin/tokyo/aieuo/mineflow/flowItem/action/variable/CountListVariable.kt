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
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.ListVariable
import tokyo.aieuo.mineflow.variable.NumberVariable

class CountListVariable(var variableName: String = "", var resultName: String = "count") : FlowItem() {

    override val id = FlowItemIds.COUNT_LIST_VARIABLE

    override val nameTranslationKey = "action.countList.name"
    override val detailTranslationKey = "action.countList.detail"
    override val detailDefaultReplaces = listOf("name", "result")

    override val category = Category.VARIABLE


    override fun isDataValid(): Boolean {
        return variableName != "" && resultName.isNotEmpty()
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(variableName, resultName))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val name = source.replaceVariables(variableName)
        val resultName = source.replaceVariables(resultName)

        val variable = source.getVariable(name) ?: Main.variableHelper.getNested(name)

        if (variable !is ListVariable) {
            throw InvalidFlowValueException(Language.get("action.countList.error.notList"))
        }

        val count = variable.getCount()
        source.addVariable(resultName, NumberVariable(count))
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            ExampleInput("@action.countList.form.name", "list", variableName, true),
            ExampleInput("@action.form.resultVariableName", "result", resultName, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        variableName = contents.getString(0)
        resultName = contents.getString(1)
    }

    override fun serializeContents(): List<Any> {
        return listOf(variableName, resultName)
    }

    override fun getAddingVariables(): DummyVariableMap {
        return mapOf(
            resultName to DummyVariable(DummyVariable.Type.NUMBER)
        )
    }
}