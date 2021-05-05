package tokyo.aieuo.mineflow.trigger.event

import cn.nukkit.entity.Entity
import cn.nukkit.event.Event
import cn.nukkit.event.player.PlayerToggleSprintEvent
import tokyo.aieuo.mineflow.variable.BoolVariable
import tokyo.aieuo.mineflow.variable.DefaultVariables
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.Variable

class PlayerToggleSprintEventTrigger(subKey: String = ""): EventTrigger(PlayerToggleSprintEvent::class, subKey) {

    override fun getTargetEntity(event: Event): Entity? = getTargetEntity(event as PlayerToggleSprintEvent)
    override fun getVariables(event: Event): Map<String, Variable<Any>> = getVariables(event as PlayerToggleSprintEvent)

    fun getTargetEntity(event: PlayerToggleSprintEvent): Entity? {
        return event.player
    }

    fun getVariables(event: PlayerToggleSprintEvent): Map<String, Variable<Any>> {
        val target = event.player
        return DefaultVariables.getPlayerVariables(target).plus(mapOf(
            "state" to BoolVariable(event.isSprinting)
        ))
    }

    override fun getVariablesDummy(): Map<String, DummyVariable<DummyVariable.Type>> {
        return mapOf(
            "target" to DummyVariable(DummyVariable.Type.PLAYER),
            "state" to DummyVariable(DummyVariable.Type.BOOLEAN)
        )
    }
}