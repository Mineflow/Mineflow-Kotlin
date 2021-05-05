package tokyo.aieuo.mineflow.trigger.event

import cn.nukkit.entity.Entity
import cn.nukkit.event.Event
import cn.nukkit.event.player.PlayerBedEnterEvent
import tokyo.aieuo.mineflow.variable.DefaultVariables
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.Variable

class PlayerBedEnterEventTrigger(subKey: String = ""): EventTrigger(PlayerBedEnterEvent::class, subKey) {

    override fun getTargetEntity(event: Event): Entity? = getTargetEntity(event as PlayerBedEnterEvent)
    override fun getVariables(event: Event): Map<String, Variable<Any>> = getVariables(event as PlayerBedEnterEvent)

    fun getTargetEntity(event: PlayerBedEnterEvent): Entity? {
        return event.player
    }

    fun getVariables(event: PlayerBedEnterEvent): Map<String, Variable<Any>> {
        val target = event.player
        val block = event.bed
        return DefaultVariables.getPlayerVariables(target).plus(DefaultVariables.getBlockVariables(block))
    }

    override fun getVariablesDummy(): Map<String, DummyVariable<DummyVariable.Type>> {
        return mapOf(
            "target" to DummyVariable(DummyVariable.Type.PLAYER),
            "block" to DummyVariable(DummyVariable.Type.BLOCK),
        )
    }
}