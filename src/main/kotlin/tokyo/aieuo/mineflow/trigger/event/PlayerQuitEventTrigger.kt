package tokyo.aieuo.mineflow.trigger.event

import cn.nukkit.entity.Entity
import cn.nukkit.event.Event
import cn.nukkit.event.player.PlayerQuitEvent
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.VariableMap
import tokyo.aieuo.mineflow.variable.DefaultVariables
import tokyo.aieuo.mineflow.variable.DummyVariable

class PlayerQuitEventTrigger(subKey: String = "") : EventTrigger(PlayerQuitEvent::class, subKey) {

    override fun getTargetEntity(event: Event): Entity? = getTargetEntity(event as PlayerQuitEvent)
    override fun getVariables(event: Event): VariableMap = getVariables(event as PlayerQuitEvent)

    fun getTargetEntity(event: PlayerQuitEvent): Entity? {
        return event.player
    }

    fun getVariables(event: PlayerQuitEvent): VariableMap {
        return DefaultVariables.getPlayerVariables(event.player)
    }

    override fun getVariablesDummy(): DummyVariableMap {
        return mapOf(
            "target" to DummyVariable(DummyVariable.Type.PLAYER)
        )
    }
}