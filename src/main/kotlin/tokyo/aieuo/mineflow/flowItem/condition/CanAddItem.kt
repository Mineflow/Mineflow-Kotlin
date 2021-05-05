package tokyo.aieuo.mineflow.flowItem.condition

import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds

class CanAddItem(player: String = "", item: String = ""): TypeItem(player, item) {

    override val id = FlowItemIds.CAN_ADD_ITEM

    override val nameTranslationKey = "condition.canAddItem.name"
    override val detailTranslationKey = "condition.canAddItem.detail"

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val item = getItem(source)

        val player = getPlayer(source)
        throwIfInvalidPlayer(player)

        val result = player.inventory.canAddItem(item)
        yield(if (result) FlowItemExecutor.Result.SUCCESS else FlowItemExecutor.Result.FAILURE)
    }
}