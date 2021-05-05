package tokyo.aieuo.mineflow.flowItem.action.scoreboard

import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.formAPI.element.Dropdown
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.utils.Scoreboard
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.obj.ScoreboardObjectVariable

class CreateScoreboardVariable(var boardId: String = "", var displayName: String = "", var displayType: String = Scoreboard.DISPLAY_SIDEBAR, var variableName: String = "board"): FlowItem() {

    override val id = FlowItemIds.CREATE_SCOREBOARD_VARIABLE

    override val nameTranslationKey = "action.createScoreboardVariable.name"
    override val detailTranslationKey = "action.createScoreboardVariable.detail"
    override val detailDefaultReplaces = listOf("result", "id", "displayName", "type")

    override val category = Category.SCOREBOARD

    private val displayTypes = listOf(Scoreboard.DISPLAY_SIDEBAR, Scoreboard.DISPLAY_LIST, Scoreboard.DISPLAY_BELOWNAME)

    override fun isDataValid(): Boolean {
        return variableName != "" && boardId != "" && displayName != "" && displayType in displayTypes
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(variableName, boardId, displayName, displayType))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val variableName = source.replaceVariables(variableName)
        val id = source.replaceVariables(boardId)
        val displayName = source.replaceVariables(displayName)

        val scoreboard = Scoreboard(displayType, id, displayName)

        val variable = ScoreboardObjectVariable(scoreboard)
        source.addVariable(variableName, variable)
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            ExampleInput("@action.createScoreboardVariable.form.id", "aieuo", boardId, true),
            ExampleInput("@action.createScoreboardVariable.form.displayName", "auieo", displayName, true),
            Dropdown("@action.createScoreboardVariable.form.type", displayTypes, displayTypes.indexOf(displayType)),
            ExampleInput("@action.form.resultVariableName", "board", variableName),
        )
    }

    override fun parseFromFormData(data: CustomFormResponseList): List<Any?> {
        return listOf(data[3], data[0], data[1], displayTypes[data.getInt(2)])
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        variableName = contents.getString(0)
        boardId = contents.getString(1)
        displayName = contents.getString(2)
        displayType = contents.getString(3)
    }

    override fun serializeContents(): List<Any> {
        return listOf(variableName, boardId, displayName, displayType)
    }

    override fun getAddingVariables(): Map<String, DummyVariable<DummyVariable.Type>> {
        return mapOf(
            variableName to DummyVariable(DummyVariable.Type.SCOREBOARD, displayName)
        )
    }
}