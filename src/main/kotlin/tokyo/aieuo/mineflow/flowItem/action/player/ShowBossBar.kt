package tokyo.aieuo.mineflow.flowItem.action.player

import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.PlayerFlowItem
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.BossBar
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.Language

class ShowBossBar(player: String = "", var title: String = "", var max: String = "", var value: String = "", var barId: String = ""): FlowItem(), PlayerFlowItem {

    override val id = FlowItemIds.SHOW_BOSSBAR

    override val nameTranslationKey = "action.showBossbar.name"
    override val detailTranslationKey = "action.showBossbar.detail"
    override val detailDefaultReplaces = listOf("player", "title", "max", "value", "id")

    override val category = Category.PLAYER

    override var playerVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setPlayerVariableName(player)
    }

    override fun isDataValid(): Boolean {
        return getPlayerVariableName() != "" && title != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getPlayerVariableName(), title, max, value, barId))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val title = source.replaceVariables(title)
        val max = source.replaceVariables(max)
        val value = source.replaceVariables(value)
        val id = source.replaceVariables(barId)

        throwIfInvalidNumber(max, 1.0)
        throwIfInvalidNumber(value)

        val player = getPlayer(source)
        throwIfInvalidPlayer(player)

        BossBar.add(player, id, title, max.toFloat(), value.toFloat() / max.toFloat())
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setPlayerVariableName(contents.getString(0))
        title = contents.getString(1)
        max = contents.getString(2)
        value = contents.getString(3)
        barId = contents.getString(4)
    }

    override fun serializeContents(): List<Any> {
        return listOf(getPlayerVariableName(), title, max, value, barId)
    }

    override fun clone(): ShowBossBar {
        val item = super.clone() as ShowBossBar
        item.playerVariableNames = playerVariableNames.toMutableMap()
        return item
    }
}