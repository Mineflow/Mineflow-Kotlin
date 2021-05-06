package tokyo.aieuo.mineflow.flowItem.action.inventory

import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds

class RemoveItemAll(player: String = "", item: String = "") : TypeItem(player, item) {

    override val id = FlowItemIds.REMOVE_ITEM_ALL

    override val nameTranslationKey = "action.removeItemAll.name"
    override val detailTranslationKey = "action.removeItemAll.detail"

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val item = getItem(source)

        val player = getPlayer(source)
        throwIfInvalidPlayer(player)

        player.inventory.remove(item)
        yield(FlowItemExecutor.Result.CONTINUE)
    }
}