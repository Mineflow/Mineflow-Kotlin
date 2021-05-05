package tokyo.aieuo.mineflow.flowItem.action.player

import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import cn.nukkit.Server
import tokyo.aieuo.mineflow.flowItem.FlowItemIds

class SendMessageToOp(message: String = ""): TypeMessage(message) {

    override val id = FlowItemIds.SEND_MESSAGE_TO_OP

    override val nameTranslationKey = "action.sendMessageToOp.name"
    override val detailTranslationKey = "action.sendMessageToOp.detail"

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val message = source.replaceVariables(message)
        Server.getInstance().onlinePlayers.forEach { (_, player) ->
            if (player.isOp) {
                player.sendMessage(message)
            }
        }
        yield(FlowItemExecutor.Result.CONTINUE)
    }
}