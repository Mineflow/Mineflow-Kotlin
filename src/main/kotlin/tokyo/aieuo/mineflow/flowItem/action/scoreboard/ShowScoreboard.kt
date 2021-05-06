package tokyo.aieuo.mineflow.flowItem.action.scoreboard

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
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language

class ShowScoreboard(player: String = "", scoreboard: String = "") : FlowItem(), PlayerFlowItem, ScoreboardFlowItem {

    override val id = FlowItemIds.SHOW_SCOREBOARD

    override val nameTranslationKey = "action.showScoreboard.name"
    override val detailTranslationKey = "action.showScoreboard.detail"
    override val detailDefaultReplaces = listOf("player", "scoreboard")

    override val category = Category.SCOREBOARD

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
        board.show(player)

        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
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

    override fun clone(): ShowScoreboard {
        val item = super.clone() as ShowScoreboard
        item.playerVariableNames = playerVariableNames.toMutableMap()
        item.scoreboardVariableNames = scoreboardVariableNames.toMutableMap()
        return item
    }
}
