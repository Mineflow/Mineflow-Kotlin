package tokyo.aieuo.mineflow.trigger.event

import cn.nukkit.entity.Entity
import cn.nukkit.event.Event
import cn.nukkit.event.block.SignChangeEvent
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.VariableMap
import tokyo.aieuo.mineflow.variable.DefaultVariables
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.ListVariable
import tokyo.aieuo.mineflow.variable.StringVariable

class SignChangeEventTrigger(subKey: String = "") : EventTrigger(SignChangeEvent::class, subKey) {

    override fun getTargetEntity(event: Event): Entity? = getTargetEntity(event as SignChangeEvent)
    override fun getVariables(event: Event): VariableMap = getVariables(event as SignChangeEvent)

    fun getTargetEntity(event: SignChangeEvent): Entity? {
        return event.player
    }

    fun getVariables(event: SignChangeEvent): VariableMap {
        val target = event.player
        val block = event.block
        return DefaultVariables.getPlayerVariables(target) + DefaultVariables.getBlockVariables(block) + mapOf(
            "sign_lines" to ListVariable(event.lines.map { StringVariable(it) })
        )
    }

    override fun getVariablesDummy(): DummyVariableMap {
        return mapOf(
            "sign_lines" to DummyVariable(DummyVariable.Type.LIST, DummyVariable.Type.STRING),
            "target" to DummyVariable(DummyVariable.Type.PLAYER),
            "block" to DummyVariable(DummyVariable.Type.BLOCK),
        )
    }
}