package tokyo.aieuo.mineflow.flowItem.action.player

import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds

class SendTip(player: String = "", message: String = "") : TypePlayerMessage(player, message) {

    override val id = FlowItemIds.SEND_TIP

    override val nameTranslationKey = "action.sendTip.name"
    override val detailTranslationKey = "action.sendTip.detail"

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val message = source.replaceVariables(message)

        val player = getPlayer(source)
        throwIfInvalidPlayer(player)

        player.sendTip(message)
        yield(FlowItemExecutor.Result.CONTINUE)
    }
}