package tokyo.aieuo.mineflow.flowItem.action.player

import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import cn.nukkit.Server
import tokyo.aieuo.mineflow.flowItem.FlowItemIds

class BroadcastMessage(message: String = ""): TypeMessage(message) {

    override val id = FlowItemIds.BROADCAST_MESSAGE

    override val nameTranslationKey = "action.broadcastMessage.name"
    override val detailTranslationKey = "action.broadcastMessage.detail"

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        message = source.replaceVariables(message)
        Server.getInstance().broadcastMessage(message)
        yield(FlowItemExecutor.Result.CONTINUE)
    }
}