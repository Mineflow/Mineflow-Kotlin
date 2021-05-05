package tokyo.aieuo.mineflow.trigger.event

import cn.nukkit.event.Event
import tokyo.aieuo.mineflow.event.ServerStartEvent
import tokyo.aieuo.mineflow.variable.DefaultVariables
import tokyo.aieuo.mineflow.variable.Variable

class ServerStartEventTrigger(subKey: String = ""): EventTrigger(ServerStartEvent::class, subKey) {

    override fun getVariables(event: Event): Map<String, Variable<Any>> = getVariables(event as ServerStartEvent)

    fun getVariables(event: ServerStartEvent): Map<String, Variable<Any>> {
        val target = getTargetEntity(event) ?: return mapOf()
        return DefaultVariables.getEntityVariables(target)
    }
}