package tokyo.aieuo.mineflow.command.subcommand

import cn.nukkit.Player
import cn.nukkit.command.CommandSender
import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.ui.RecipeForm
import tokyo.aieuo.mineflow.utils.Language

class RecipeCommand : MineflowSubcommand {
    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender !is Player) return
        if (args.isEmpty()) {
            RecipeForm.sendMenu(sender)
            return
        }

        when (args[0]) {
            "add" -> RecipeForm.sendAddRecipe(sender, args.getOrNull(1) ?: "", args.getOrNull(2) ?: "")
            "edit" -> RecipeForm.sendSelectRecipe(sender, args.getOrNull(1) ?: "")
            "list" -> RecipeForm.sendRecipeList(sender)
            "execute" -> {
                if (args.size <= 1) {
                    sender.sendMessage("Usage: /mineflow recipe execute <name> [group]")
                    return
                }
                val path = (if (args.size > 2) "${args[2]}/" else "") + args[1]
                val (name, group) = Main.recipeManager.parseName(path)

                val recipe = Main.recipeManager.get(name, group)
                if (recipe === null) {
                    sender.sendMessage(Language.get("action.executeRecipe.notFound"))
                    return
                }

                recipe.executeAllTargets(sender)
            }
            else -> sender.sendMessage(Language.get("command.recipe.usage"))
        }
    }
}