package tokyo.aieuo.mineflow.flowItem.action.script

import tokyo.aieuo.mineflow.exception.InvalidFlowValueException
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.ConfigHolder
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.obj.ConfigObjectVariable

class CreateConfigVariable(var fileName: String = "", var variableName: String = "config"): FlowItem() {

    override val id = FlowItemIds.CREATE_CONFIG_VARIABLE

    override val nameTranslationKey = "action.createConfigVariable.name"
    override val detailTranslationKey = "action.createConfigVariable.detail"
    override val detailDefaultReplaces = listOf("config", "name")

    override val category = Category.SCRIPT

    override fun isDataValid(): Boolean {
        return variableName != "" && fileName != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(variableName, fileName))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val name = source.replaceVariables(variableName)
        val file = source.replaceVariables(fileName)
        if (Regex("[.¥/:?<>|*\"]").containsMatchIn(file)) {
            throw InvalidFlowValueException(Language.get("form.recipe.invalidName"))
        }

        val variable = ConfigObjectVariable(ConfigHolder.getConfig(file))
        source.addVariable(name, variable)
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            ExampleInput("@action.createConfigVariable.form.name", "config", fileName, true),
            ExampleInput("@action.form.resultVariableName", "config", variableName, true),
        )
    }

    override fun parseFromFormData(data: CustomFormResponseList): List<Any?> {
        return listOf(data[1], data[0])
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        variableName = contents.getString(0)
        fileName = contents.getString(1)
    }

    override fun serializeContents(): List<Any> {
        return listOf(variableName, fileName)
    }

    override fun getAddingVariables(): Map<String, DummyVariable<DummyVariable.Type>> {
        return mapOf(
            variableName to DummyVariable(DummyVariable.Type.CONFIG, fileName)
        )
    }
}