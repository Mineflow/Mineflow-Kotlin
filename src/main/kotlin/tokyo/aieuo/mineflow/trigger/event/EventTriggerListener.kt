package tokyo.aieuo.mineflow.trigger.event

import cn.nukkit.Server
import cn.nukkit.event.Event
import cn.nukkit.event.EventPriority
import cn.nukkit.event.Listener
import cn.nukkit.plugin.MethodEventExecutor
import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.trigger.TriggerHolder
import tokyo.aieuo.mineflow.trigger.Triggers

class EventTriggerListener : Listener {

    private val registeredEvents = mutableListOf<Class<out Event>>()

    fun registerEvent(event: Class<out Event>) {
        if (event in registeredEvents) return

        val pluginManager = Server.getInstance().pluginManager
        pluginManager.registerEvent(
            event,
            this,
            EventPriority.NORMAL,
            MethodEventExecutor(EventTriggerListener::class.java.getMethod("onEvent", Event::class.java)),
            Main.instance
        )
        registeredEvents.add(event)
    }

    fun onEvent(event: Event) {
        val eventName = event.eventName

        if (TriggerHolder.existsRecipe(Triggers.EVENT, eventName)) {
            val trigger = EventTrigger.create(eventName)
            val recipes = TriggerHolder.getRecipes(trigger)
            val variables = trigger.getVariables(event)
            recipes?.executeAll(trigger.getTargetEntity(event), variables, event)
        }
    }
}