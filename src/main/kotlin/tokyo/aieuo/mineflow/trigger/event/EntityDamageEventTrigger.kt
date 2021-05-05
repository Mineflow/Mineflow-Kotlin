package tokyo.aieuo.mineflow.trigger.event

import cn.nukkit.entity.Entity
import cn.nukkit.event.Event
import cn.nukkit.event.entity.EntityDamageByEntityEvent
import cn.nukkit.event.entity.EntityDamageEvent
import tokyo.aieuo.mineflow.variable.DefaultVariables
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.NumberVariable
import tokyo.aieuo.mineflow.variable.Variable

class EntityDamageEventTrigger(subKey: String = ""): EventTrigger(EntityDamageEvent::class, subKey) {

    override fun getTargetEntity(event: Event): Entity? = getTargetEntity(event as EntityDamageEvent)
    override fun getVariables(event: Event): Map<String, Variable<Any>> = getVariables(event as EntityDamageEvent)

    fun getTargetEntity(event: EntityDamageEvent): Entity? {
        return event.entity
    }

    fun getVariables(event: EntityDamageEvent): Map<String, Variable<Any>> {
        val target = event.entity
        val variables = DefaultVariables.getEntityVariables(target, "target").toMutableMap()
        variables["damage"] = NumberVariable(event.damage)
        variables["cause"] = NumberVariable(event.cause.ordinal)
        return if (event is EntityDamageByEntityEvent) {
            variables.plus(DefaultVariables.getEntityVariables(event.damager, "damager"))
        } else {
            variables
        }
    }

    override fun getVariablesDummy(): Map<String, DummyVariable<DummyVariable.Type>> {
        return mapOf(
            "target" to DummyVariable(DummyVariable.Type.PLAYER),
            "killer" to DummyVariable(DummyVariable.Type.PLAYER),
        )
    }
}