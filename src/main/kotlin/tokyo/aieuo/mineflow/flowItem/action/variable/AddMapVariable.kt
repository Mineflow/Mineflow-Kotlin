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
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.MapVariable
import tokyo.aieuo.mineflow.variable.Variable

class AddMapVariable(
    var variableName: String = "",
    var variableKey: String = "",
    var variableValue: String = "",
    var isLocal: Boolean = true
) : FlowItem() {

    override val id = FlowItemIds.ADD_MAP_VARIABLE

    override val nameTranslationKey = "action.addMapVariable.name"
    override val detailTranslationKey = "action.addMapVariable.detail"
    override val detailDefaultReplaces = listOf("name", "scope", "key", "value")

    override val category = Category.VARIABLE

    override fun isDataValid(): Boolean {
        return variableName != "" && variableKey != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(
            detailTranslationKey,
            listOf(variableName, if (isLocal) "local" else "global", variableKey, variableValue)
        )
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val helper = Main.variableHelper
        val name = source.replaceVariables(variableName)
        val key = source.replaceVariables(variableKey)
        val value = variableValue

        val variable = if (isLocal) source.getVariable(name) else helper.get(name)

        val addVariable = if (helper.isVariableString(value)) {
            val inside = value.substring(1, value.length - 1)
            source.getVariable(inside) ?: helper.get(inside).let {
                if (it === null) {
                    val v = helper.replaceVariables(value, source.getVariables())
                    Variable.create(helper.currentType(v), helper.getType(v))
                } else {
                    it
                }
            }
        } else {
            val v = helper.replaceVariables(value, source.getVariables())
            Variable.create(helper.currentType(v), helper.getType(v))
        }

        if (variable === null || addVariable === null) {
            throw InvalidFlowValueException(Language.get("variable.notFound", listOf(name)))
        }
        if (variable !is MapVariable) {
            throw InvalidFlowValueException(
                Language.get(
                    "action.addListVariable.error.existsOtherType",
                    listOf(name, variable.toString())
                )
            )
        }

        val contents = variable.value.toMutableMap()
        contents[key] = addVariable
        variable.value = contents
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            ExampleInput("@action.variable.form.name", "aieuo", variableName, true),
            ExampleInput("@action.variable.form.key", "auieo", variableKey, false),
            ExampleInput("@action.variable.form.value", "aeiuo", variableValue, false),
            Toggle("@action.variable.form.global", !isLocal),
        )
    }

    override fun parseFromFormData(data: CustomFormResponseList): List<Any?> {
        // TODO: AddListVariableのように区切って複数同時に追加できるようにする
        return listOf(data[0], data[1], data[2], !data.getBoolean(3))
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        variableName = contents.getString(0)
        variableKey = contents.getString(1)
        variableValue = contents.getString(2)
        isLocal = contents.getBoolean(3)
    }

    override fun serializeContents(): List<Any> {
        return listOf(variableName, variableKey, variableValue, isLocal)
    }

    override fun getAddingVariables(): DummyVariableMap {
        return mapOf(
            variableName to DummyVariable(DummyVariable.Type.MAP)
        )
    }
}