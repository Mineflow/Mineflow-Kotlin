package tokyo.aieuo.mineflow.trigger.event

import cn.nukkit.entity.Entity
import cn.nukkit.event.Event
import cn.nukkit.event.inventory.InventoryPickupItemEvent
import cn.nukkit.inventory.PlayerInventory
import tokyo.aieuo.mineflow.variable.DefaultVariables
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.Variable
import tokyo.aieuo.mineflow.variable.obj.ItemObjectVariable

class InventoryPickupItemEventTrigger(subKey: String = ""): EventTrigger(InventoryPickupItemEvent::class, subKey) {

    override fun getTargetEntity(event: Event): Entity? = getTargetEntity(event as InventoryPickupItemEvent)
    override fun getVariables(event: Event): Map<String, Variable<Any>> = getVariables(event as InventoryPickupItemEvent)

    fun getTargetEntity(event: InventoryPickupItemEvent): Entity? {
        return event.inventory.let { if (it is PlayerInventory) it.holder else null }
    }

    fun getVariables(event: InventoryPickupItemEvent): Map<String, Variable<Any>> {
        var variables = mapOf<String, Variable<Any>>()
        val inventory = event.inventory
        if (inventory is PlayerInventory) {
            variables = variables.plus(DefaultVariables.getEntityVariables(inventory.holder))
        }
        return variables.plus(mapOf(
            "item" to ItemObjectVariable(event.item.item),
        ))
    }

    override fun getVariablesDummy(): Map<String, DummyVariable<DummyVariable.Type>> {
        return mapOf(
            "target" to DummyVariable(DummyVariable.Type.PLAYER),
            "item" to DummyVariable(DummyVariable.Type.ITEM),
        )
    }
}