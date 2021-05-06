package tokyo.aieuo.mineflow.flowItem.action.common

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
import tokyo.aieuo.mineflow.variable.StringVariable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class GetDate(var format: String = "H:i:s", var resultName: String = "date") : FlowItem() {

    override val id = FlowItemIds.GET_DATE

    override val nameTranslationKey = "action.getDate.name"
    override val detailTranslationKey = "action.getDate.detail"
    override val detailDefaultReplaces = listOf("format", "result")

    override val category = Category.COMMON

    override fun isDataValid(): Boolean {
        return format != "" && resultName != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(format, resultName))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val format = source.replaceVariables(format)
        val resultName = source.replaceVariables(resultName)

        val date = try {
            LocalDateTime.now().format(DateTimeFormatter.ofPattern(format))
        } catch (e: IllegalArgumentException) {
            throw InvalidFlowValueException(e.message ?: "IllegalArgumentException")
        }
        source.addVariable(resultName, StringVariable(date))
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            ExampleInput("@action.getDate.form.format", "H:i:s", format, true),
            ExampleInput("@action.form.resultVariableName", "date", resultName, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        format = contents.getString(0)
        resultName = contents.getString(1)
    }

    override fun serializeContents(): List<Any> {
        return listOf(format, resultName)
    }

    override fun getAddingVariables(): DummyVariableMap {
        return mapOf(
            resultName to DummyVariable(DummyVariable.Type.STRING)
        )
    }
}