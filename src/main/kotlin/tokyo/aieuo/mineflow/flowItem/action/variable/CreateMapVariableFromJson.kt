package tokyo.aieuo.mineflow.flowItem.action.variable

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.ObjectMapper
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

class CreateMapVariableFromJson(var variableName: String = "", var json: String = "", var isLocal: Boolean = true) :
    FlowItem() {

    override val id = FlowItemIds.CREATE_MAP_VARIABLE_FROM_JSON

    override val nameTranslationKey = "action.createMapVariableFromJson.name"
    override val detailTranslationKey = "action.createMapVariableFromJson.detail"
    override val detailDefaultReplaces = listOf("name", "scope", "json")

    override val category = Category.VARIABLE

    override fun isDataValid(): Boolean {
        return variableName != "" && json != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(variableName, if (isLocal) "local" else "global", json))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val helper = Main.variableHelper
        val name = source.replaceVariables(variableName)

        val value = try {
            ObjectMapper().readValue(json, mutableMapOf<String, Any>().javaClass)
        } catch (e: JsonParseException) {
            throw InvalidFlowValueException(e.message ?: "JsonSyntaxException")
        }

        val variable = MapVariable(Main.variableHelper.toVariableArray(value))

        if (isLocal) {
            source.addVariable(name, variable)
        } else {
            helper.add(name, variable)
        }
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            ExampleInput("@action.variable.form.name", "aieuo", variableName, true),
            ExampleInput("@action.variable.form.value", "aeiuo", json, true),
            Toggle("@action.variable.form.global", !isLocal),
        )
    }

    override fun parseFromFormData(data: CustomFormResponseList): List<Any?> {
        return listOf(data[0], data[1], !data.getBoolean(2))
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        variableName = contents.getString(0)
        json = contents.getString(1)
        isLocal = contents.getBoolean(2)
    }

    override fun serializeContents(): List<Any> {
        return listOf(variableName, json, isLocal)
    }

    override fun getAddingVariables(): DummyVariableMap {
        return mapOf(
            variableName to DummyVariable(DummyVariable.Type.MAP, json)
        )
    }
}