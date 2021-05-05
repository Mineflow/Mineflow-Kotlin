package tokyo.aieuo.mineflow.trigger.event

import cn.nukkit.entity.Entity
import cn.nukkit.event.Event
import cn.nukkit.event.inventory.CraftItemEvent
import tokyo.aieuo.mineflow.variable.DefaultVariables
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.ListVariable
import tokyo.aieuo.mineflow.variable.Variable
import tokyo.aieuo.mineflow.variable.obj.ItemObjectVariable

class CraftItemEventTrigger(subKey: String = ""): EventTrigger(CraftItemEvent::class, subKey) {

    override fun getTargetEntity(event: Event): Entity? = getTargetEntity(event as CraftItemEvent)
    override fun getVariables(event: Event): Map<String, Variable<Any>> = getVariables(event as CraftItemEvent)

    fun getTargetEntity(event: CraftItemEvent): Entity? {
        return event.player
    }

    fun getVariables(event: CraftItemEvent): Map<String, Variable<Any>> {
        val target = event.player
        val inputs = event.input.map { item -> ItemObjectVariable(item) }.toMutableList()
        val outputs = mutableListOf(ItemObjectVariable(event.recipe.result))
        return DefaultVariables.getPlayerVariables(target).plus(mapOf(
            "inputs" to ListVariable(inputs),
            "outputs" to ListVariable(outputs),
        ))
    }

    override fun getVariablesDummy(): Map<String, DummyVariable<DummyVariable.Type>> {
        return mapOf(
            "target" to DummyVariable(DummyVariable.Type.PLAYER),
            "inputs" to DummyVariable(DummyVariable.Type.LIST, DummyVariable.Type.ITEM),
            "outputs" to DummyVariable(DummyVariable.Type.LIST, DummyVariable.Type.ITEM),
        )
    }
}