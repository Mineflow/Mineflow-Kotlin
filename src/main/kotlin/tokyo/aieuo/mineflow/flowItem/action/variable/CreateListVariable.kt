package tokyo.aieuo.mineflow.flowItem.action.variable

import tokyo.aieuo.mineflow.Main
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

class CreateListVariable(var variableValue: List<String> = listOf(), var variableName: String = "", var isLocal: Boolean = true): FlowItem() {

    override val id = FlowItemIds.CREATE_LIST_VARIABLE

    override val nameTranslationKey = "action.createListVariable.name"
    override val detailTranslationKey = "action.createListVariable.detail"
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

        val contents = mutableListOf<Variable<Any>>()
        for (value in values) {
            if (value == "") continue

            contents.add(if (helper.isVariableString(value)) {
                source.getVariable(value.substring(1, value.length - 1)) ?: helper.get(value.substring(1, value.length - 1))?.let {
                    return@let it
                }
                val v = helper.replaceVariables(value, source.getVariables())
                Variable.create(helper.currentType(v), helper.getType(v)) ?: continue
            } else {
                val v = helper.replaceVariables(value, source.getVariables())
                Variable.create(helper.currentType(v), helper.getType(v)) ?: continue
            })
        }

        val variable = ListVariable(contents)
        if (isLocal) {
            source.addVariable(name, variable)
        } else {
            helper.add(name, variable)
        }
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            ExampleInput("@action.variable.form.name", "aieuo", variableName, true),
            ExampleInput("@action.variable.form.value", "aiueo", variableValue.joinToString(","), true),
            Toggle("@action.variable.form.global", !isLocal),
        )
    }

    override fun parseFromFormData(data: CustomFormResponseList): List<Any?> {
        return listOf(data[0], data.getString(1).split(",").map { it.trim() }, !data.getBoolean(2))
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
            variableName to DummyVariable(DummyVariable.Type.LIST)
        )
    }
}