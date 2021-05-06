package tokyo.aieuo.mineflow.flowItem.action.variable

import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.exception.InvalidFlowValueException
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.Toggle
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.ListVariable
import tokyo.aieuo.mineflow.variable.MapVariable

class DeleteListVariableContent(
    var variableName: String = "",
    var variableKey: String = "",
    var isLocal: Boolean = true
) : FlowItem() {

    override val id = FlowItemIds.DELETE_LIST_VARIABLE_CONTENT

    override val nameTranslationKey = "action.removeContent.name"
    override val detailTranslationKey = "action.removeContent.detail"
    override val detailDefaultReplaces = listOf("name", "scope", "key")

    override val category = Category.VARIABLE

    override fun isDataValid(): Boolean {
        return variableName != "" && variableKey != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(variableName, if (isLocal) "local" else "global", variableKey))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val helper = Main.variableHelper
        val name = source.replaceVariables(variableName)
        val key = source.replaceVariables(variableKey)

        val variable = if (isLocal) source.getVariable(name) else helper.getNested(name)
        if (variable === null) {
            throw InvalidFlowValueException(Language.get("variable.notFound", listOf(name)))
        }

        when (variable) { // TODO check variable is ListVariable
            is MapVariable -> {
                val values = variable.value.toMutableMap()
                values.remove(key)
                variable.value = values
            }
            is ListVariable -> {
                val values = variable.value.toMutableList()
                values.removeAt(key.toInt())
                variable.value = values
            }
            else -> throw InvalidFlowValueException(
                Language.get(
                    "action.addListVariable.error.existsOtherType",
                    listOf(name, variable.toString())
                )
            )
        }
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            ExampleInput("@action.variable.form.name", "aieuo", variableName, true),
            ExampleInput("@action.variable.form.key", "auieo", variableKey, true),
            Toggle("@action.variable.form.global", !isLocal),
        )
    }

    override fun parseFromFormData(data: CustomFormResponseList): List<Any?> {
        return listOf(data[0], data[1], !data.getBoolean(2))
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        variableName = contents.getString(0)
        variableKey = contents.getString(1)
        isLocal = contents.getBoolean(2)
    }

    override fun serializeContents(): List<Any> {
        return listOf(variableName, variableKey, isLocal)
    }
}