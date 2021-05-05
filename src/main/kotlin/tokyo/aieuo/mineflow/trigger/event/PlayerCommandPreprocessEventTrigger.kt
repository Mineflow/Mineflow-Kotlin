package tokyo.aieuo.mineflow.trigger.event

import cn.nukkit.entity.Entity
import cn.nukkit.event.Event
import cn.nukkit.event.player.PlayerCommandPreprocessEvent
import tokyo.aieuo.mineflow.variable.DefaultVariables
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.StringVariable
import tokyo.aieuo.mineflow.variable.Variable

class PlayerCommandPreprocessEventTrigger(subKey: String = ""): EventTrigger(PlayerCommandPreprocessEvent::class, subKey) {

    override fun getTargetEntity(event: Event): Entity? = getTargetEntity(event as PlayerCommandPreprocessEvent)
    override fun getVariables(event: Event): Map<String, Variable<Any>> = getVariables(event as PlayerCommandPreprocessEvent)

    fun getTargetEntity(event: PlayerCommandPreprocessEvent): Entity? {
        return event.player
    }

    fun getVariables(event: PlayerCommandPreprocessEvent): Map<String, Variable<Any>> {
        val target = event.player
        return DefaultVariables.getPlayerVariables(target).plus(DefaultVariables.getCommandVariables(event.message.substring(1))).plus(mapOf(
            "message" to StringVariable(event.message)
        ))
    }

    override fun getVariablesDummy(): Map<String, DummyVariable<DummyVariable.Type>> {
        return mapOf(
            "target" to DummyVariable(DummyVariable.Type.PLAYER),
            "message" to DummyVariable(DummyVariable.Type.STRING),
            "cmd" to DummyVariable(DummyVariable.Type.STRING),
            "args" to DummyVariable(DummyVariable.Type.LIST, DummyVariable.Type.STRING),
        )
    }
}