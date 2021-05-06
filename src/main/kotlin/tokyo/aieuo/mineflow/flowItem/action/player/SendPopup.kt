package tokyo.aieuo.mineflow.flowItem.action.player

import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds

class SendPopup(player: String = "", message: String = "") : TypePlayerMessage(player, message) {

    override val id = FlowItemIds.SEND_POPUP

    override val nameTranslationKey = "action.sendPopup.name"
    override val detailTranslationKey = "action.sendPopup.detail"

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val message = source.replaceVariables(message)

        val player = getPlayer(source)
        throwIfInvalidPlayer(player)

        player.sendPopup(message)
        yield(FlowItemExecutor.Result.CONTINUE)
    }
}