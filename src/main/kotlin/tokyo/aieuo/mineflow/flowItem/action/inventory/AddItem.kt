package tokyo.aieuo.mineflow.flowItem.action.inventory

import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds

class AddItem(player: String = "", item: String = "") : TypeItem(player, item) {

    override val id = FlowItemIds.ADD_ITEM

    override val nameTranslationKey = "action.addItem.name"
    override val detailTranslationKey = "action.addItem.detail"

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val item = getItem(source)

        val player = getPlayer(source)
        throwIfInvalidPlayer(player)

        player.inventory.addItem(item)
        yield(FlowItemExecutor.Result.CONTINUE)
    }
}