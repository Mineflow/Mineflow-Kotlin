package tokyo.aieuo.mineflow.trigger.event

import cn.nukkit.event.Event
import tokyo.aieuo.mineflow.event.ServerStartEvent
import tokyo.aieuo.mineflow.utils.VariableMap
import tokyo.aieuo.mineflow.variable.DefaultVariables

class ServerStartEventTrigger(subKey: String = "") : EventTrigger(ServerStartEvent::class, subKey) {

    override fun getVariables(event: Event): VariableMap = getVariables(event as ServerStartEvent)

    fun getVariables(event: ServerStartEvent): VariableMap {
        val target = getTargetEntity(event) ?: return mapOf()
        return DefaultVariables.getEntityVariables(target)
    }
}