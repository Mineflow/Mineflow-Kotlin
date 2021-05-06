package tokyo.aieuo.mineflow.trigger.event

import cn.nukkit.entity.Entity
import cn.nukkit.event.Event
import cn.nukkit.event.player.PlayerChatEvent
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.VariableMap
import tokyo.aieuo.mineflow.variable.DefaultVariables
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.StringVariable

class PlayerChatEventTrigger(subKey: String = "") : EventTrigger(PlayerChatEvent::class, subKey) {

    override fun getTargetEntity(event: Event): Entity? = getTargetEntity(event as PlayerChatEvent)
    override fun getVariables(event: Event): VariableMap = getVariables(event as PlayerChatEvent)

    fun getTargetEntity(event: PlayerChatEvent): Entity? {
        return event.player
    }

    fun getVariables(event: PlayerChatEvent): VariableMap {
        val target = event.player
        return DefaultVariables.getPlayerVariables(target) + mapOf(
            "messages" to StringVariable(event.message)
        )
    }

    override fun getVariablesDummy(): DummyVariableMap {
        return mapOf(
            "target" to DummyVariable(DummyVariable.Type.PLAYER),
            "message" to DummyVariable(DummyVariable.Type.STRING),
        )
    }
}