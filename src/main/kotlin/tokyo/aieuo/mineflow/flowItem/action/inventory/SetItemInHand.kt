package tokyo.aieuo.mineflow.flowItem.action.inventory

import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds

class SetItemInHand(player: String = "", item: String = ""): TypeItem(player, item) {

    override val id = FlowItemIds.SET_ITEM_IN_HAND

    override val nameTranslationKey = "action.setItemInHand.name"
    override val detailTranslationKey = "action.setItemInHand.detail"

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val item = getItem(source)

        val player = getPlayer(source)
        throwIfInvalidPlayer(player)

        player.inventory.itemInHand = item
        yield(FlowItemExecutor.Result.CONTINUE)
    }
}