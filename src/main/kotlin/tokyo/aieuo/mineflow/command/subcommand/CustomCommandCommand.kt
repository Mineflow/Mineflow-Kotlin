package tokyo.aieuo.mineflow.command.subcommand

import cn.nukkit.Player
import cn.nukkit.command.CommandSender
import tokyo.aieuo.mineflow.ui.CommandForm
import tokyo.aieuo.mineflow.utils.Language

class CustomCommandCommand : MineflowSubcommand {
    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender !is Player) return
        if (args.isEmpty()) {
            CommandForm.sendMenu(sender)
            return
        }

        when (args[0]) {
            "add" -> CommandForm.sendAddCommand(
                sender,
                args.getOrNull(1) ?: "",
                args.getOrNull(2) ?: "",
                args.getOrNull(3)?.toIntOrNull() ?: 0
            )
            "edit" -> CommandForm.sendSelectCommand(sender, args.getOrNull(1) ?: "")
            "list" -> CommandForm.sendCommandList(sender)
            else -> sender.sendMessage(Language.get("command.command.usage"))
        }
    }
}