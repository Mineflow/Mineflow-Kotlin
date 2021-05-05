package tokyo.aieuo.mineflow.flowItem.action.player

import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds

class SendMessage(player: String = "", message: String = ""): TypePlayerMessage(player, message) {

    override val id = FlowItemIds.SEND_MESSAGE

    override val nameTranslationKey = "action.sendMessage.name"
    override val detailTranslationKey = "action.sendMessage.detail"

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        message = source.replaceVariables(message)

        val player = getPlayer(source)
        throwIfInvalidPlayer(player)

        player.sendMessage(message)
        yield(FlowItemExecutor.Result.CONTINUE)
    }
}