package tokyo.aieuo.mineflow.ui.trigger

import cn.nukkit.Player
import tokyo.aieuo.mineflow.recipe.Recipe
import tokyo.aieuo.mineflow.trigger.Trigger

interface TriggerForm {

    fun sendAddedTriggerMenu(player: Player, recipe: Recipe, trigger: Trigger, messages: List<String> = listOf())

    fun sendMenu(player: Player, recipe: Recipe)
}