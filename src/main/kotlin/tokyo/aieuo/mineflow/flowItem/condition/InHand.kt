package tokyo.aieuo.mineflow.flowItem.condition

import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds

class InHand(player: String = "", item: String = "") : TypeItem(player, item) {

    override val id = FlowItemIds.IN_HAND

    override val nameTranslationKey = "condition.inHand.name"
    override val detailTranslationKey = "condition.inHand.detail"

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val item = getItem(source)

        val player = getPlayer(source)
        throwIfInvalidPlayer(player)

        val hand = player.inventory.itemInHand

        val result = (hand.id == item.id
                && hand.damage == item.damage
                && hand.count >= item.count
                && (!item.hasCustomName() || hand.name == item.name)
                && (item.lore.isEmpty() || item.lore.contentEquals(hand.lore))
                && (item.enchantments.isEmpty() || item.enchantments.contentEquals(hand.enchantments))
                )
        yield(if (result) FlowItemExecutor.Result.SUCCESS else FlowItemExecutor.Result.FAILURE)
    }
}