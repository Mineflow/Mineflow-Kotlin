package tokyo.aieuo.mineflow.flowItem.action.variable

import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.Toggle
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.MapVariable
import tokyo.aieuo.mineflow.variable.Variable

class CreateMapVariable(var variableName: String = "", var variableKey: List<String> = listOf(), var variableValue: List<String> = listOf(), var isLocal: Boolean = true): FlowItem() {

    override val id = FlowItemIds.CREATE_MAP_VARIABLE

    override val nameTranslationKey = "action.createMapVariable.name"
    override val detailTranslationKey = "action.createMapVariable.detail"
    override val detailDefaultReplaces = listOf("name", "scope", "key", "value")

    override val category = Category.VARIABLE

    override fun isDataValid(): Boolean {
        return variableName != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(variableName, if (isLocal) "local" else "global", variableKey.joinToString(","), variableValue.joinToString(",")))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val helper = Main.variableHelper
        val name = source.replaceVariables(variableName)
        val keys = variableKey.map { source.replaceVariables(it) }
        val values = variableValue

        val contents = mutableMapOf<String, Variable<Any>>()
        for (i in keys.indices) {
            val key = keys[i]
            var value = values.getOrElse(i) { "" }
            if (key == "") continue

            contents[key] = if (helper.isVariableString(value)) {
                source.getVariable(value.substring(1, value.length - 1)) ?: helper.get(value.substring(1, value.length - 1))?.let {
                    return@let it
                }
                value = helper.replaceVariables(value, source.getVariables())
                Variable.create(helper.currentType(value), helper.getType(value)) ?: continue
            } else {
                value = helper.replaceVariables(value, source.getVariables())
                Variable.create(helper.currentType(value), helper.getType(value)) ?: continue
            }
        }

        val variable = MapVariable(contents)
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
            ExampleInput("@action.variable.form.key", "auieo", variableKey.joinToString(","), false),
            ExampleInput("@action.variable.form.value", "aeiuo", variableValue.joinToString(","), false),
            Toggle("@action.variable.form.global", !isLocal),
        )
    }

    override fun parseFromFormData(data: CustomFormResponseList): List<Any?> {
        val name = data.getString(0)
        val key = data.getString(1).split(",").map { it.trim() }
        val value = data.getString(2).split(",").map { it.trim() }
        return listOf(name, key, value, !data.getBoolean(3))
    }

    @Suppress("UNCHECKED_CAST")
    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        variableName = contents.getString(0)
        variableKey = contents[1] as List<String>
        variableValue = contents[2] as List<String>
        isLocal = contents.getBoolean(3)
    }

    override fun serializeContents(): List<Any> {
        return listOf(variableName, variableKey, variableValue, isLocal)
    }

    override fun getAddingVariables(): Map<String, DummyVariable<DummyVariable.Type>> {
        return mapOf(
            variableName to DummyVariable(DummyVariable.Type.MAP)
        )
    }
}