package tokyo.aieuo.mineflow.flowItem.action.scoreboard

import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.ScoreboardFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ScoreboardVariableDropdown
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable

class RemoveScoreboardScore(scoreboard: String = "", var scoreName: String = ""): FlowItem(), ScoreboardFlowItem {

    override val id = FlowItemIds.REMOVE_SCOREBOARD_SCORE

    override val nameTranslationKey = "action.removeScore.name"
    override val detailTranslationKey = "action.removeScore.detail"
    override val detailDefaultReplaces = listOf("scoreboard", "name")

    override val category = Category.SCOREBOARD

    override var scoreboardVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setScoreboardVariableName(scoreboard)
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getScoreboardVariableName(), scoreName))
    }

    override fun isDataValid(): Boolean {
        return getScoreboardVariableName() != "" && scoreName != ""
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val name = source.replaceVariables(scoreName)

        val board = getScoreboard(source)

        board.removeScore(name)
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            ScoreboardVariableDropdown(variables, getScoreboardVariableName()),
            ExampleInput("@action.setScore.form.name", "aieuo", scoreName, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setScoreboardVariableName(contents.getString(0))
        scoreName = contents.getString(1)
    }

    override fun serializeContents(): List<Any> {
        return listOf(getScoreboardVariableName(), scoreName)
    }

    override fun clone(): RemoveScoreboardScore {
        val item = super.clone() as RemoveScoreboardScore
        item.scoreboardVariableNames = scoreboardVariableNames.toMutableMap()
        return item
    }
}
