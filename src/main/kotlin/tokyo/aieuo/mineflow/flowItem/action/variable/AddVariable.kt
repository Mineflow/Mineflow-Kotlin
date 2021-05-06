package tokyo.aieuo.mineflow.flowItem.action.variable

import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.exception.InvalidFlowValueException
import tokyo.aieuo.mineflow.exception.InvalidFormValueException
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.formAPI.element.Dropdown
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.Toggle
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.utils.is_numeric
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.NumberVariable
import tokyo.aieuo.mineflow.variable.StringVariable
import tokyo.aieuo.mineflow.variable.Variable

class AddVariable(
    var variableName: String = "",
    var variableValue: String = "",
    var variableType: Int = Variable.STRING,
    var isLocal: Boolean = true
) : FlowItem() {

    override val id = FlowItemIds.ADD_VARIABLE

    override val nameTranslationKey = "action.addVariable.name"
    override val detailTranslationKey = "action.addVariable.detail"
    override val detailDefaultReplaces = listOf("name", "value", "type", "scope")

    override val category = Category.VARIABLE

    private val variableTypes = listOf("string", "number")
    private val dummyVariableTypes = listOf(DummyVariable.Type.STRING, DummyVariable.Type.NUMBER)

    override fun isDataValid(): Boolean {
        return variableName != "" && variableValue != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(
            detailTranslationKey,
            listOf(variableName, variableValue, variableTypes[variableType], if (isLocal) "local" else "global")
        )
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val name = source.replaceVariables(variableName)
        val value = source.replaceVariables(variableValue)

        val variable = when (variableType) {
            Variable.STRING -> StringVariable(value)
            Variable.NUMBER -> {
                throwIfInvalidNumber(value)
                NumberVariable(value.toFloat())
            }
            else -> throw InvalidFlowValueException(Language.get("action.error.recipe"))
        }

        if (isLocal) {
            source.addVariable(name, variable)
        } else {
            Main.variableHelper.add(name, variable)
        }
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            ExampleInput("@action.variable.form.name", "aieuo", variableName, true),
            ExampleInput("@action.variable.form.value", "aeiuo", variableValue, true),
            Dropdown("@action.variable.form.type", variableTypes, variableType),
            Toggle("@action.variable.form.global", !isLocal),
        )
    }

    override fun parseFromFormData(data: CustomFormResponseList): List<Any?> {
        val containsVariable = Main.variableHelper.containsVariable(data.getString(1))
        if (data[2] == Variable.NUMBER && !containsVariable && !is_numeric(data.getString(1))) {
            throw InvalidFormValueException(Language.get("action.error.notNumber", listOf(data.getString(1))), 1)
        }

        return listOf(data[0], data[1], data[2], !data.getBoolean(3))
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        variableName = contents.getString(0)
        variableValue = contents.getString(1)
        variableType = contents.getInt(2)
        isLocal = contents.getBoolean(3)
    }

    override fun serializeContents(): List<Any> {
        return listOf(variableName, variableValue, variableType, isLocal)
    }

    override fun getAddingVariables(): DummyVariableMap {
        return mapOf(
            variableName to DummyVariable(dummyVariableTypes[variableType], variableValue)
        )
    }
}