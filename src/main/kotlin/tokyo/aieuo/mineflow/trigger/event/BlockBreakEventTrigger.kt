package tokyo.aieuo.mineflow.trigger.event

import cn.nukkit.entity.Entity
import cn.nukkit.event.Event
import cn.nukkit.event.block.BlockBreakEvent
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.VariableMap
import tokyo.aieuo.mineflow.variable.DefaultVariables
import tokyo.aieuo.mineflow.variable.DummyVariable

class BlockBreakEventTrigger(subKey: String = "") : EventTrigger(BlockBreakEvent::class, subKey) {

    override fun getTargetEntity(event: Event): Entity? = getTargetEntity(event as BlockBreakEvent)
    override fun getVariables(event: Event): VariableMap = getVariables(event as BlockBreakEvent)

    fun getTargetEntity(event: BlockBreakEvent): Entity? {
        return event.player
    }

    fun getVariables(event: BlockBreakEvent): VariableMap {
        val target = event.player
        val block = event.block
        return DefaultVariables.getPlayerVariables(target) + DefaultVariables.getBlockVariables(block)
    }

    override fun getVariablesDummy(): DummyVariableMap {
        return mapOf(
            "target" to DummyVariable(DummyVariable.Type.PLAYER),
            "block" to DummyVariable(DummyVariable.Type.BLOCK),
        )
    }
}