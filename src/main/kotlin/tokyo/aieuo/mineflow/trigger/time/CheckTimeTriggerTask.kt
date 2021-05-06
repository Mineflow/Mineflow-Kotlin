package tokyo.aieuo.mineflow.trigger.time

import cn.nukkit.Server
import cn.nukkit.scheduler.Task
import tokyo.aieuo.mineflow.trigger.TriggerHolder
import tokyo.aieuo.mineflow.utils.date

class CheckTimeTriggerTask : Task() {

    override fun onRun(currentTick: Int) {
        val trigger = TimeTrigger.create(date("HH"), date("mm"))
        if (TriggerHolder.existsRecipe(trigger)) {
            val recipes = TriggerHolder.getRecipes(trigger)
            val variables = trigger.getVariables()
            recipes?.executeAll(null, variables)
        }
    }

    companion object {
        fun start() {
            val seconds = date("ss").toInt()
            Server.getInstance().scheduler.scheduleDelayedRepeatingTask(
                CheckTimeTriggerTask(),
                20 * (60 - seconds),
                20 * 60
            )
        }
    }
}