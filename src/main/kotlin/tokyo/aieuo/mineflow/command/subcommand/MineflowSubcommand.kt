package tokyo.aieuo.mineflow.command.subcommand

import cn.nukkit.command.CommandSender

interface MineflowSubcommand {
    fun execute(sender: CommandSender, args: Array<String>)
}