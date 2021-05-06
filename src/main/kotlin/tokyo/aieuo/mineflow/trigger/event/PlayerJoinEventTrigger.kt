package tokyo.aieuo.mineflow.trigger.event

import cn.nukkit.entity.Entity
import cn.nukkit.event.Event
import cn.nukkit.event.player.PlayerJoinEvent
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.VariableMap
import tokyo.aieuo.mineflow.variable.DefaultVariables
import tokyo.aieuo.mineflow.variable.DummyVariable

class PlayerJoinEventTrigger(subKey: String = "") : EventTrigger(PlayerJoinEvent::class, subKey) {

    override fun getTargetEntity(event: Event): Entity? = getTargetEntity(event as PlayerJoinEvent)
    override fun getVariables(event: Event): VariableMap = getVariables(event as PlayerJoinEvent)

    fun getTargetEntity(event: PlayerJoinEvent): Entity? {
        return event.player
    }

    fun getVariables(event: PlayerJoinEvent): VariableMap {
        return DefaultVariables.getPlayerVariables(event.player)
    }

    override fun getVariablesDummy(): DummyVariableMap {
        return mapOf(
            "target" to DummyVariable(DummyVariable.Type.PLAYER)
        )
    }
}