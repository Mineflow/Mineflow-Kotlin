package tokyo.aieuo.mineflow.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.command.PluginIdentifiableCommand
import cn.nukkit.plugin.Plugin
import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.command.subcommand.CustomCommandCommand
import tokyo.aieuo.mineflow.command.subcommand.LanguageCommand
import tokyo.aieuo.mineflow.command.subcommand.RecipeCommand
import tokyo.aieuo.mineflow.ui.HomeForm
import tokyo.aieuo.mineflow.ui.SettingForm
import tokyo.aieuo.mineflow.ui.customForm.CustomFormForm
import tokyo.aieuo.mineflow.utils.Language

class MineflowCommand : Command(
    "mineflow",
    Language.get("command.mineflow.description"),
    Language.get("command.mineflow.usage"),
    arrayOf("mf")
), PluginIdentifiableCommand {

    init {
        permission = "mineflow.command.mineflow"
    }

    override fun getPlugin(): Plugin {
        return Main.instance
    }

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
        if (!testPermission(sender) || sender is MineflowConsoleCommandSender) return false

        if (args.isEmpty() && sender is Player) {
            HomeForm.sendMenu(sender)
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage(Language.get("command.mineflow.usage.console"))
            return true
        }

        when (args[0]) {
            "language" -> (LanguageCommand()).execute(sender, args.copyOfRange(1, args.size))
            "recipe" -> {
                if (sender !is Player) {
                    sender.sendMessage(Language.get("command.console"))
                    return true
                }
                (RecipeCommand()).execute(sender, args.copyOfRange(1, args.size))
            }
            "command" -> {
                if (sender !is Player) {
                    sender.sendMessage(Language.get("command.console"))
                    return true
                }
                (CustomCommandCommand()).execute(sender, args.copyOfRange(1, args.size))
            }
            "form" -> {
                if (sender !is Player) {
                    sender.sendMessage(Language.get("command.console"))
                    return true
                }
                (CustomFormForm).sendMenu(sender)
            }
            "settings" -> {
                if (sender !is Player) {
                    sender.sendMessage(Language.get("command.console"))
                    return true
                }
                SettingForm.sendMenu(sender)
            }
            "permission" -> {
                if (args.size <= 1) {
                    sender.sendMessage(Language.get("command.permission.usage"))
                    return true
                }
                val config = Main.instance.playerSettings
                val permission = if (sender is Player) config.getInt("${sender.name}.permission", 0) else 2
                if (permission < args[1].toIntOrNull() ?: 0) {
                    sender.sendMessage(Language.get("command.permission.permission.notEnough"))
                    return true
                }
                config.set("${args[0]}.permission", args[1].toIntOrNull() ?: 0)
                config.save()
                sender.sendMessage(Language.get("form.changed"))
            }
            "seerecipe" -> {
                if (args.isEmpty()) {
                    sender.sendMessage("Usage: /mineflow seerecipe <name> [group]")
                    return true
                }
                val path = (if (args.size > 2) "${args[2]}/" else "") + args[1]
                val (name, group) = Main.recipeManager.parseName(path)

                val recipe = Main.recipeManager.get(name, group)
                if (recipe === null) {
                    sender.sendMessage(Language.get("action.executeRecipe.notFound"))
                    return true
                }

                sender.sendMessage(recipe.getDetail())
            }
            else -> {
                if (sender !is Player) {
                    sender.sendMessage(Language.get("command.mineflow.usage.console"))
                    return true
                }
                HomeForm.sendMenu(sender)
            }
        }
        return true
    }
}