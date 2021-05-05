package tokyo.aieuo.mineflow.flowItem.action.variable

import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.exception.InvalidFlowValueException
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.element.Toggle
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.ListVariable
import tokyo.aieuo.mineflow.variable.Variable

class AddListVariable(var variableValue: List<String> = listOf(), var variableName: String = "", var isLocal: Boolean = true): FlowItem() {

    override val id = FlowItemIds.ADD_LIST_VARIABLE

    override val nameTranslationKey = "action.addListVariable.name"
    override val detailTranslationKey = "action.addListVariable.detail"
    override val detailDefaultReplaces = listOf("name", "scope", "value")

    override val category = Category.VARIABLE

    override fun isDataValid(): Boolean {
        return variableName != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(variableName, if (isLocal) "local" else "global", variableValue.joinToString(",")))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val helper = Main.variableHelper
        val name = source.replaceVariables(variableName)
        val values = variableValue

        val variable = if (isLocal) source.getVariable(name) else helper.get(name)
        if (variable === null) {
            throw InvalidFlowValueException(Language.get("variable.notFound", listOf(name)))
        }
        if (variable !is ListVariable) {
            throw InvalidFlowValueException(Language.get("action.addListVariable.error.existsOtherType", listOf(name, variable.toString())))
        }

        val contents = variable.value.toMutableList()
        for (value in values) {
            val addVariable = if (helper.isVariableString(value)) {
                source.getVariable(value.substring(1, value.length - 1)) ?: helper.get(value.substring(1, value.length - 1))?.let {
                    return@let it
                }
                val v = helper.replaceVariables(value, source.getVariables())
                Variable.create(helper.currentType(v), helper.getType(v))
            } else {
                val v = helper.replaceVariables(value, source.getVariables())
                Variable.create(helper.currentType(v), helper.getType(v))
            } ?: continue

            contents.add(addVariable)
        }
        variable.value = contents
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            ExampleInput("@action.variable.form.name", "aieuo", variableName, true),
            ExampleInput("@action.variable.form.value", "aiueo", variableValue.joinToString(","), false),
            Toggle("@action.variable.form.global", !isLocal),
        )
    }

    override fun parseFromFormData(data: CustomFormResponseList): List<Any?> {
        return listOf(data[0], (data.getString(1)).split(",").map { it.trim() }, !data.getBoolean(2))
    }

    @Suppress("UNCHECKED_CAST")
    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        variableName = contents.getString(0)
        variableValue = contents[1] as List<String>
        isLocal = contents.getBoolean(2)
    }

    override fun serializeContents(): List<Any> {
        return listOf(variableName, variableValue, isLocal)
    }

    override fun getAddingVariables(): Map<String, DummyVariable<DummyVariable.Type>> {
        return mapOf(
            variableName to DummyVariable(DummyVariable.Type.LIST, "[${variableValue.joinToString(",")}]")
        )
    }
}