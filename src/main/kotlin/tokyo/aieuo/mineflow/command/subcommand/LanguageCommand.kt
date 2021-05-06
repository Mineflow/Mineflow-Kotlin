package tokyo.aieuo.mineflow.command.subcommand

import cn.nukkit.command.CommandSender
import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.utils.Language

class LanguageCommand : MineflowSubcommand {
    override fun execute(sender: CommandSender, args: Array<String>) {
        if (args.isEmpty()) {
            sender.sendMessage(Language.get("command.language.usage"))
            return
        }

        if (!Language.isAvailableLanguage(args[0])) {
            sender.sendMessage(
                Language.get(
                    "command.language.notfound",
                    listOf(args[0], Language.getAvailableLanguages().joinToString(", "))
                )
            )
            return
        }
        Language.language = args[0]

        val config = Main.instance.config
        config.set("language", args[0])
        config.save()

        sender.sendMessage(Language.get("language.selected", listOf(Language.get("language.name"))))
    }
}