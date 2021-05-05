package tokyo.aieuo.mineflow.trigger.event

import cn.nukkit.Player
import cn.nukkit.entity.Entity
import cn.nukkit.event.Event
import cn.nukkit.event.entity.EntityDamageByEntityEvent
import cn.nukkit.event.entity.EntityDeathEvent
import tokyo.aieuo.mineflow.variable.DefaultVariables
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.Variable

class EntityDeathEventTrigger(subKey: String = ""): EventTrigger(EntityDeathEvent::class, subKey) {

    override fun getTargetEntity(event: Event): Entity? = getTargetEntity(event as EntityDeathEvent)
    override fun getVariables(event: Event): Map<String, Variable<Any>> = getVariables(event as EntityDeathEvent)

    fun getTargetEntity(event: EntityDeathEvent): Entity? {
        return event.entity
    }

    fun getVariables(event: EntityDeathEvent): Map<String, Variable<Any>> {
        val target = event.entity
        var variables = DefaultVariables.getEntityVariables(target)
        val cause = target.lastDamageCause
        if (cause is EntityDamageByEntityEvent) {
            val killer = cause.damager
            if (killer is Player) {
                variables = variables.plus(DefaultVariables.getPlayerVariables(killer, "killer"))
            }
        }
        return variables
    }

    override fun getVariablesDummy(): Map<String, DummyVariable<DummyVariable.Type>> {
        return mapOf(
            "target" to DummyVariable(DummyVariable.Type.ENTITY),
        )
    }
}