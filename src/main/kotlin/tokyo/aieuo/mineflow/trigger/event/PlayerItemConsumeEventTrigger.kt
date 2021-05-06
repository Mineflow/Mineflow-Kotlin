package tokyo.aieuo.mineflow.trigger.event

import cn.nukkit.entity.Entity
import cn.nukkit.event.Event
import cn.nukkit.event.player.PlayerItemConsumeEvent
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.VariableMap
import tokyo.aieuo.mineflow.variable.DefaultVariables
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.obj.ItemObjectVariable

class PlayerItemConsumeEventTrigger(subKey: String = "") : EventTrigger(PlayerItemConsumeEvent::class, subKey) {

    override fun getTargetEntity(event: Event): Entity? = getTargetEntity(event as PlayerItemConsumeEvent)

    override fun getVariables(event: Event): VariableMap = getVariables(event as PlayerItemConsumeEvent)

    fun getTargetEntity(event: PlayerItemConsumeEvent): Entity? {
        return event.player
    }

    fun getVariables(event: PlayerItemConsumeEvent): VariableMap {
        val target = event.player
        val item = event.item
        return DefaultVariables.getPlayerVariables(target) + mapOf(
            "item" to ItemObjectVariable(item),
        )
    }

    override fun getVariablesDummy(): DummyVariableMap {
        return mapOf(
            "target" to DummyVariable(DummyVariable.Type.PLAYER),
            "item" to DummyVariable(DummyVariable.Type.ITEM),
        )
    }
}