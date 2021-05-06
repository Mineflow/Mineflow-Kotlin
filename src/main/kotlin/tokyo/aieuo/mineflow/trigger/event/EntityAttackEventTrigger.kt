package tokyo.aieuo.mineflow.trigger.event

import cn.nukkit.entity.Entity
import cn.nukkit.event.Event
import tokyo.aieuo.mineflow.event.EntityAttackEvent
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.VariableMap
import tokyo.aieuo.mineflow.variable.DefaultVariables
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.NumberVariable

class EntityAttackEventTrigger(subKey: String = "") :
    EventTrigger(EntityAttackEvent::class, subKey) {

    override fun getTargetEntity(event: Event): Entity? =
        getTargetEntity(event as EntityAttackEvent)

    override fun getVariables(event: Event): VariableMap = getVariables(event as EntityAttackEvent)

    fun getTargetEntity(event: EntityAttackEvent): Entity? {
        return event.damageEvent.damager
    }

    fun getVariables(event: EntityAttackEvent): VariableMap {
        val entityDamageEvent = event.damageEvent
        val target = entityDamageEvent.entity
        return DefaultVariables.getEntityVariables(entityDamageEvent.damager, "target").plus(
            mapOf(
                "damage" to NumberVariable(entityDamageEvent.damage),
                "cause" to NumberVariable(entityDamageEvent.cause.ordinal),
            )
        ).plus(DefaultVariables.getEntityVariables(target, "damaged"))
    }

    override fun getVariablesDummy(): DummyVariableMap {
        return mapOf(
            "target" to DummyVariable(DummyVariable.Type.PLAYER),
            "damage" to DummyVariable(DummyVariable.Type.NUMBER),
            "cause" to DummyVariable(DummyVariable.Type.NUMBER),
            "damaged" to DummyVariable(DummyVariable.Type.PLAYER),
        )
    }
}