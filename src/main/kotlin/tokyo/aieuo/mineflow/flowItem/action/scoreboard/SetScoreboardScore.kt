package tokyo.aieuo.mineflow.flowItem.action.scoreboard

import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.ScoreboardFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleNumberInput
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ScoreboardVariableDropdown
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable

class SetScoreboardScore(scoreboard: String = "", var scoreName: String = "", var score: String = ""): FlowItem(), ScoreboardFlowItem {

    override val id = FlowItemIds.SET_SCOREBOARD_SCORE

    override val nameTranslationKey = "action.setScore.name"
    override val detailTranslationKey = "action.setScore.detail"
    override val detailDefaultReplaces = listOf("scoreboard", "name", "score")

    override val category = Category.SCOREBOARD

    override var scoreboardVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setScoreboardVariableName(scoreboard)
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getScoreboardVariableName(), scoreName, score))
    }

    override fun isDataValid(): Boolean {
        return getScoreboardVariableName() != "" && scoreName != "" && score != ""
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val name = source.replaceVariables(scoreName)
        val score = source.replaceVariables(score)

        throwIfInvalidNumber(score)

        val board = getScoreboard(source)

        board.setScore(name, score.toInt())
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            ScoreboardVariableDropdown(variables, getScoreboardVariableName()),
            ExampleInput("@action.setScore.form.name", "aieuo", scoreName, true),
            ExampleNumberInput("@action.setScore.form.score", "100", score, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setScoreboardVariableName(contents.getString(0))
        scoreName = contents.getString(1)
        score = contents.getString(2)
    }

    override fun serializeContents(): List<Any> {
        return listOf(getScoreboardVariableName(), scoreName, score)
    }

    override fun clone(): SetScoreboardScore {
        val item = super.clone() as SetScoreboardScore
        item.scoreboardVariableNames = scoreboardVariableNames.toMutableMap()
        return item
    }
}
