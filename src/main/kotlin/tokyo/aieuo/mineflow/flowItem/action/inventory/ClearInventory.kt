package tokyo.aieuo.mineflow.flowItem.action.inventory

import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.PlayerFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.PlayerVariableDropdown
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language

class ClearInventory(player: String = "") : FlowItem(), PlayerFlowItem {

    override val id = FlowItemIds.CLEAR_INVENTORY

    override val nameTranslationKey = "action.clearInventory.name"
    override val detailTranslationKey = "action.clearInventory.detail"
    override val detailDefaultReplaces = listOf("player")

    override val category = Category.INVENTORY

    override var playerVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setPlayerVariableName(player)
    }

    override fun isDataValid(): Boolean {
        return getPlayerVariableName() != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getPlayerVariableName()))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val player = getPlayer(source)
        throwIfInvalidPlayer(player)

        player.inventory.clearAll()
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            PlayerVariableDropdown(variables, getPlayerVariableName()),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setPlayerVariableName(contents.getString(0))
    }

    override fun serializeContents(): List<Any> {
        return listOf(getPlayerVariableName())
    }

    override fun clone(): ClearInventory {
        val item = super.clone() as ClearInventory
        item.playerVariableNames = playerVariableNames.toMutableMap()
        return item
    }
}