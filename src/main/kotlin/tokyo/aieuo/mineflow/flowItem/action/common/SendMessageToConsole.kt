package tokyo.aieuo.mineflow.flowItem.action.common

import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.action.player.TypeMessage
import tokyo.aieuo.mineflow.utils.Category

class SendMessageToConsole(message: String = "") : TypeMessage(message) {

    override val id = FlowItemIds.SEND_MESSAGE_TO_CONSOLE

    override val nameTranslationKey = "action.sendMessageToConsole.name"
    override val detailTranslationKey = "action.sendMessageToConsole.detail"

    override val category = Category.COMMON

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val message = source.replaceVariables(message)
        Main.instance.logger.info(message)
        yield(FlowItemExecutor.Result.CONTINUE)
    }
}