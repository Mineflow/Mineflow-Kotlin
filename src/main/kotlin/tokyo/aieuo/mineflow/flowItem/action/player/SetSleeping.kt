package tokyo.aieuo.mineflow.flowItem.action.player

import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.PlayerFlowItem
import tokyo.aieuo.mineflow.flowItem.base.PositionFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.PlayerVariableDropdown
import tokyo.aieuo.mineflow.formAPI.element.mineflow.PositionVariableDropdown
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language

class SetSleeping(player: String = "", position: String = "") : FlowItem(), PlayerFlowItem, PositionFlowItem {

    override val id = FlowItemIds.SET_SLEEPING

    override val nameTranslationKey = "action.setSleeping.name"
    override val detailTranslationKey = "action.setSleeping.detail"
    override val detailDefaultReplaces = listOf("player", "position")

    override val category = Category.PLAYER

    override var playerVariableNames: MutableMap<String, String> = mutableMapOf()
    override var positionVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setPlayerVariableName(player)
        setPositionVariableName(position)
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getPlayerVariableName(), getPositionVariableName()))
    }

    override fun isDataValid(): Boolean {
        return getPlayerVariableName() != "" && getPositionVariableName() != ""
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val player = getPlayer(source)
        throwIfInvalidPlayer(player)

        val position = getPosition(source)

        player.sleepOn(position)
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            PlayerVariableDropdown(variables, getPlayerVariableName()),
            PositionVariableDropdown(variables, getPositionVariableName()),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setPlayerVariableName(contents.getString(0))
        setPositionVariableName(contents.getString(1))
    }

    override fun serializeContents(): List<Any> {
        return listOf(getPlayerVariableName(), getPositionVariableName())
    }

    override fun clone(): SetSleeping {
        val item = super.clone() as SetSleeping
        item.playerVariableNames = playerVariableNames.toMutableMap()
        item.positionVariableNames = positionVariableNames.toMutableMap()
        return item
    }
}
