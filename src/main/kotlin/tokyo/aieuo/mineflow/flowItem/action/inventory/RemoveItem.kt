package tokyo.aieuo.mineflow.flowItem.action.inventory

import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds

class RemoveItem(player: String = "", item: String = ""): TypeItem(player, item) {

    override val id = FlowItemIds.REMOVE_ITEM

    override val nameTranslationKey = "action.removeItem.name"
    override val detailTranslationKey = "action.removeItem.detail"

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val item = getItem(source)

        val player = getPlayer(source)
        throwIfInvalidPlayer(player)

        player.inventory.removeItem(item)
        yield(FlowItemExecutor.Result.CONTINUE)
    }
}