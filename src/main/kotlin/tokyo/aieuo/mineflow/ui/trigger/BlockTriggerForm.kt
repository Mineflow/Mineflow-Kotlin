package tokyo.aieuo.mineflow.ui.trigger

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.level.Position
import tokyo.aieuo.mineflow.formAPI.ListForm
import tokyo.aieuo.mineflow.formAPI.element.Button
import tokyo.aieuo.mineflow.recipe.Recipe
import tokyo.aieuo.mineflow.trigger.Trigger
import tokyo.aieuo.mineflow.trigger.Triggers
import tokyo.aieuo.mineflow.ui.RecipeForm
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.utils.Session

object BlockTriggerForm : TriggerForm {

    override fun sendAddedTriggerMenu(player: Player, recipe: Recipe, trigger: Trigger, messages: List<String>) {
        (ListForm(Language.get("form.trigger.addedTriggerMenu.title", listOf(recipe.name, trigger.key))))
            .setContent(trigger.toString())
            .addButtons(
                Button("@form.back") { RecipeForm.sendTriggerList(player, recipe) },
                Button("@form.delete") { BaseTriggerForm.sendConfirmDelete(player, recipe, trigger) },
                Button("@trigger.block.warp") {
                    val pos = trigger.key.split(",")
                    val level = Server.getInstance().getLevelByName(pos[3])
                    if (level === null) {
                        sendAddedTriggerMenu(player, recipe, trigger, listOf("@trigger.block.world.notfound"))
                        return@Button
                    }
                    player.teleport(Position(pos[0].toDouble(), pos[1].toDouble(), pos[2].toDouble(), level))
                },
            ).addMessages(messages).show(player)
    }

    override fun sendMenu(player: Player, recipe: Recipe) {
        (ListForm(Language.get("form.trigger.triggerMenu.title", listOf(recipe.name, Triggers.BLOCK))))
            .addButtons(
                Button("@form.back") { BaseTriggerForm.sendSelectTriggerType(player, recipe) },
                Button("@form.add") {
                    Session.getSession(player).let {
                        it.set("blockTriggerAction", "add")
                        it.set("blockTriggerRecipe", recipe)
                    }
                    player.sendMessage(Language.get("trigger.block.add.touch"))
                },
            ).show(player)
    }
}