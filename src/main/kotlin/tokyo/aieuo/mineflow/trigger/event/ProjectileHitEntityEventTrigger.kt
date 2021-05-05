package tokyo.aieuo.mineflow.trigger.event

import cn.nukkit.entity.Entity
import cn.nukkit.event.Event
import tokyo.aieuo.mineflow.event.ProjectileHitEntityEvent
import tokyo.aieuo.mineflow.variable.DefaultVariables
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.Variable

class ProjectileHitEntityEventTrigger(subKey: String = ""): EventTrigger(ProjectileHitEntityEvent::class, subKey) {

    override fun getTargetEntity(event: Event): Entity = getTargetEntity(event as ProjectileHitEntityEvent)
    override fun getVariables(event: Event): Map<String, Variable<Any>> = getVariables(event as ProjectileHitEntityEvent)

    fun getTargetEntity(event: ProjectileHitEntityEvent): Entity {
        return event.entityHit
    }

    fun getVariables(event: ProjectileHitEntityEvent): Map<String, Variable<Any>> {
        return DefaultVariables.getEntityVariables(event.entityHit).plus(
            DefaultVariables.getEntityVariables(event.entity, "projective")
        )
    }

    override fun getVariablesDummy(): Map<String, DummyVariable<DummyVariable.Type>> {
        return mapOf(
            "target" to DummyVariable(DummyVariable.Type.ENTITY),
            "projectile" to DummyVariable(DummyVariable.Type.ENTITY),
        )
    }
}