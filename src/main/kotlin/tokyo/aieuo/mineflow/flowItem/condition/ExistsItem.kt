package tokyo.aieuo.mineflow.flowItem.condition

import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds

class ExistsItem(player: String = "", item: String = ""): TypeItem(player, item) {

    override val id = FlowItemIds.EXISTS_ITEM

    override val nameTranslationKey = "condition.existsItem.name"
    override val detailTranslationKey = "condition.existsItem.detail"

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val item = getItem(source)

        val player = getPlayer(source)
        throwIfInvalidPlayer(player)

        val result = player.inventory.contains(item)
        yield(if (result) FlowItemExecutor.Result.SUCCESS else FlowItemExecutor.Result.FAILURE)
    }
}