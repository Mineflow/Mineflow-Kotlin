package tokyo.aieuo.mineflow.trigger.event

import cn.nukkit.entity.Entity
import cn.nukkit.event.Event
import tokyo.aieuo.mineflow.event.PlayerExhaustEvent
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.VariableMap
import tokyo.aieuo.mineflow.variable.DefaultVariables
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.NumberVariable

class PlayerExhaustEventTrigger(subKey: String = "") : EventTrigger(PlayerExhaustEvent::class, subKey) {

    override fun getTargetEntity(event: Event): Entity? = getTargetEntity(event as PlayerExhaustEvent)

    override fun getVariables(event: Event): VariableMap = getVariables(event as PlayerExhaustEvent)

    fun getTargetEntity(event: PlayerExhaustEvent): Entity? {
        return event.player
    }

    fun getVariables(event: PlayerExhaustEvent): VariableMap {
        val target = event.player
        return DefaultVariables.getEntityVariables(target).plus(
            mapOf(
                "amount" to NumberVariable(event.amount),
            )
        )
    }

    override fun getVariablesDummy(): DummyVariableMap {
        return mapOf(
            "amount" to DummyVariable(DummyVariable.Type.NUMBER),
        )
    }
}