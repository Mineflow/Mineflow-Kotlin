package tokyo.aieuo.mineflow.trigger.event

import cn.nukkit.entity.Entity
import cn.nukkit.event.Event
import cn.nukkit.event.player.PlayerDropItemEvent
import tokyo.aieuo.mineflow.variable.DefaultVariables
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.Variable
import tokyo.aieuo.mineflow.variable.obj.ItemObjectVariable

class PlayerDropItemEventTrigger(subKey: String = ""): EventTrigger(PlayerDropItemEvent::class, subKey) {

    override fun getTargetEntity(event: Event): Entity? = getTargetEntity(event as PlayerDropItemEvent)
    override fun getVariables(event: Event): Map<String, Variable<Any>> = getVariables(event as PlayerDropItemEvent)

    fun getTargetEntity(event: PlayerDropItemEvent): Entity? {
        return event.player
    }

    fun getVariables(event: PlayerDropItemEvent): Map<String, Variable<Any>> {
        val target = event.player
        val item = event.item
        return DefaultVariables.getPlayerVariables(target).plus(mapOf(
            "item" to ItemObjectVariable(item),
        ))
    }

    override fun getVariablesDummy(): Map<String, DummyVariable<DummyVariable.Type>> {
        return mapOf(
            "target" to DummyVariable(DummyVariable.Type.PLAYER),
            "item" to DummyVariable(DummyVariable.Type.ITEM),
        )
    }
}