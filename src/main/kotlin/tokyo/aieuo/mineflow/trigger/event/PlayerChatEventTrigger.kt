package tokyo.aieuo.mineflow.trigger.event

import cn.nukkit.entity.Entity
import cn.nukkit.event.Event
import cn.nukkit.event.player.PlayerChatEvent
import tokyo.aieuo.mineflow.variable.DefaultVariables
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.StringVariable
import tokyo.aieuo.mineflow.variable.Variable

class PlayerChatEventTrigger(subKey: String = ""): EventTrigger(PlayerChatEvent::class, subKey) {

    override fun getTargetEntity(event: Event): Entity? = getTargetEntity(event as PlayerChatEvent)
    override fun getVariables(event: Event): Map<String, Variable<Any>> = getVariables(event as PlayerChatEvent)

    fun getTargetEntity(event: PlayerChatEvent): Entity? {
        return event.player
    }

    fun getVariables(event: PlayerChatEvent): Map<String, Variable<Any>> {
        val target = event.player
        return DefaultVariables.getPlayerVariables(target).plus(mapOf(
            "messages" to StringVariable(event.message)
        ))
    }

    override fun getVariablesDummy(): Map<String, DummyVariable<DummyVariable.Type>> {
        return mapOf(
            "target" to DummyVariable(DummyVariable.Type.PLAYER),
            "message" to DummyVariable(DummyVariable.Type.STRING),
        )
    }
}