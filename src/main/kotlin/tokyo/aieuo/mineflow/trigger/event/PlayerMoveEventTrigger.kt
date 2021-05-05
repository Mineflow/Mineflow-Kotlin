package tokyo.aieuo.mineflow.trigger.event

import cn.nukkit.entity.Entity
import cn.nukkit.event.Event
import cn.nukkit.event.player.PlayerMoveEvent
import tokyo.aieuo.mineflow.variable.DefaultVariables
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.Variable
import tokyo.aieuo.mineflow.variable.obj.LocationObjectVariable

class PlayerMoveEventTrigger(subKey: String = ""): EventTrigger(PlayerMoveEvent::class, subKey) {

    override fun getTargetEntity(event: Event): Entity? = getTargetEntity(event as PlayerMoveEvent)
    override fun getVariables(event: Event): Map<String, Variable<Any>> = getVariables(event as PlayerMoveEvent)

    fun getTargetEntity(event: PlayerMoveEvent): Entity? {
        return event.player
    }

    fun getVariables(event: PlayerMoveEvent): Map<String, Variable<Any>> {
        val target = event.player
        return DefaultVariables.getPlayerVariables(target).plus(mapOf(
            "move_from" to LocationObjectVariable(event.from),
            "move_to" to LocationObjectVariable(event.to)
        ))
    }

    override fun getVariablesDummy(): Map<String, DummyVariable<DummyVariable.Type>> {
        return mapOf(
            "move_from" to DummyVariable(DummyVariable.Type.LOCATION),
            "move_to" to DummyVariable(DummyVariable.Type.LOCATION),
            "target" to DummyVariable(DummyVariable.Type.PLAYER),
        )
    }
}