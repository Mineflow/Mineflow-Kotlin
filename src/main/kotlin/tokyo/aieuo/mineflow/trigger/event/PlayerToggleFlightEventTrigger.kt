package tokyo.aieuo.mineflow.trigger.event

import cn.nukkit.entity.Entity
import cn.nukkit.event.Event
import cn.nukkit.event.player.PlayerToggleFlightEvent
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.VariableMap
import tokyo.aieuo.mineflow.variable.BoolVariable
import tokyo.aieuo.mineflow.variable.DefaultVariables
import tokyo.aieuo.mineflow.variable.DummyVariable

class PlayerToggleFlightEventTrigger(subKey: String = "") : EventTrigger(PlayerToggleFlightEvent::class, subKey) {

    override fun getTargetEntity(event: Event): Entity? = getTargetEntity(event as PlayerToggleFlightEvent)

    override fun getVariables(event: Event): VariableMap = getVariables(event as PlayerToggleFlightEvent)

    fun getTargetEntity(event: PlayerToggleFlightEvent): Entity? {
        return event.player
    }

    fun getVariables(event: PlayerToggleFlightEvent): VariableMap {
        val target = event.player
        return DefaultVariables.getPlayerVariables(target).plus(
            mapOf(
                "state" to BoolVariable(event.isFlying)
            )
        )
    }

    override fun getVariablesDummy(): DummyVariableMap {
        return mapOf(
            "target" to DummyVariable(DummyVariable.Type.PLAYER),
            "state" to DummyVariable(DummyVariable.Type.BOOLEAN)
        )
    }
}