package tokyo.aieuo.mineflow.flowItem.action.scoreboard

import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.ScoreboardFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleNumberInput
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ScoreboardVariableDropdown
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language

class RemoveScoreboardScoreName(scoreboard: String = "", var score: String = "") : FlowItem(), ScoreboardFlowItem {

    override val id = FlowItemIds.REMOVE_SCOREBOARD_SCORE_NAME

    override val nameTranslationKey = "action.removeScoreName.name"
    override val detailTranslationKey = "action.removeScoreName.detail"
    override val detailDefaultReplaces = listOf("scoreboard", "score")

    override val category = Category.SCOREBOARD

    override var scoreboardVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setScoreboardVariableName(scoreboard)
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getScoreboardVariableName(), score))
    }

    override fun isDataValid(): Boolean {
        return getScoreboardVariableName() != "" && score != ""
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val score = source.replaceVariables(score)

        throwIfInvalidNumber(score)

        val board = getScoreboard(source)

        board.removeScoreName(score.toInt())
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            ScoreboardVariableDropdown(variables, getScoreboardVariableName()),
            ExampleNumberInput("@action.setScore.form.score", "100", score, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setScoreboardVariableName(contents.getString(0))
        score = contents.getString(1)
    }

    override fun serializeContents(): List<Any> {
        return listOf(getScoreboardVariableName(), score)
    }

    override fun clone(): RemoveScoreboardScoreName {
        val item = super.clone() as RemoveScoreboardScoreName
        item.scoreboardVariableNames = scoreboardVariableNames.toMutableMap()
        return item
    }
}
