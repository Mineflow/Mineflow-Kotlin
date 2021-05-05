package tokyo.aieuo.mineflow.flowItem.action.player

import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.PlayerFlowItem
import tokyo.aieuo.mineflow.flowItem.base.ScoreboardFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.PlayerVariableDropdown
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ScoreboardVariableDropdown
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable

class HideScoreboard(player: String = "", scoreboard: String = ""): FlowItem(), PlayerFlowItem, ScoreboardFlowItem {

    override val id = FlowItemIds.HIDE_SCOREBOARD

    override val nameTranslationKey = "action.hideScoreboard.name"
    override val detailTranslationKey = "action.hideScoreboard.detail"
    override val detailDefaultReplaces = listOf("player", "scoreboard")

    override val category = Category.PLAYER

    override var playerVariableNames: MutableMap<String, String> = mutableMapOf()
    override var scoreboardVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setPlayerVariableName(player)
        setScoreboardVariableName(scoreboard)
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getPlayerVariableName(), getScoreboardVariableName()))
    }

    override fun isDataValid(): Boolean {
        return getPlayerVariableName() != "" && getScoreboardVariableName() != ""
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val player = getPlayer(source)
        throwIfInvalidPlayer(player)

        val board = getScoreboard(source)

        board.hide(player)
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            PlayerVariableDropdown(variables, getPlayerVariableName()),
            ScoreboardVariableDropdown(variables, getScoreboardVariableName()),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setPlayerVariableName(contents.getString(0))
        setScoreboardVariableName(contents.getString(1))
    }

    override fun serializeContents(): List<Any> {
        return listOf(getPlayerVariableName(), getScoreboardVariableName())
    }

    override fun clone(): HideScoreboard {
        val item = super.clone() as HideScoreboard
        item.playerVariableNames = playerVariableNames.toMutableMap()
        item.scoreboardVariableNames = scoreboardVariableNames.toMutableMap()
        return item
    }
}
