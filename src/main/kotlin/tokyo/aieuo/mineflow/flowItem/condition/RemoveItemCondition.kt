package tokyo.aieuo.mineflow.flowItem.condition

import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds

class RemoveItemCondition(player: String = "", item: String = "") : TypeItem(player, item) {

    override val id = FlowItemIds.REMOVE_ITEM_CONDITION

    override val nameTranslationKey = "condition.removeItem.name"
    override val detailTranslationKey = "condition.removeItem.detail"

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val item = getItem(source)

        val player = getPlayer(source)
        throwIfInvalidPlayer(player)

        if (player.inventory.contains(item)) {
            player.inventory.removeItem(item)
            yield(FlowItemExecutor.Result.SUCCESS)
        } else {
            yield(FlowItemExecutor.Result.FAILURE)
        }
    }
}