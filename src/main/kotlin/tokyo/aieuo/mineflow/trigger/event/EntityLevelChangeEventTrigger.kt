package tokyo.aieuo.mineflow.trigger.event

import cn.nukkit.entity.Entity
import cn.nukkit.event.Event
import cn.nukkit.event.entity.EntityLevelChangeEvent
import tokyo.aieuo.mineflow.variable.DefaultVariables
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.Variable
import tokyo.aieuo.mineflow.variable.obj.WorldObjectVariable

class EntityLevelChangeEventTrigger(subKey: String = ""): EventTrigger(EntityLevelChangeEvent::class, subKey) {

    override fun getTargetEntity(event: Event): Entity? = getTargetEntity(event as EntityLevelChangeEvent)
    override fun getVariables(event: Event): Map<String, Variable<Any>> = getVariables(event as EntityLevelChangeEvent)

    fun getTargetEntity(event: EntityLevelChangeEvent): Entity? {
        return event.entity
    }

    fun getVariables(event: EntityLevelChangeEvent): Map<String, Variable<Any>> {
        val target = event.entity
        return DefaultVariables.getEntityVariables(target).plus(mapOf(
            "origin_world" to WorldObjectVariable(event.origin),
            "target_world" to WorldObjectVariable(event.target)
        ))
    }

    override fun getVariablesDummy(): Map<String, DummyVariable<DummyVariable.Type>> {
        return mapOf(
            "target" to DummyVariable(DummyVariable.Type.PLAYER),
            "origin_world" to DummyVariable(DummyVariable.Type.WORLD),
            "target_world" to DummyVariable(DummyVariable.Type.WORLD),
        )
    }
}