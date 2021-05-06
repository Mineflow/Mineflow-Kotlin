package tokyo.aieuo.mineflow.trigger.event

import cn.nukkit.event.Event
import cn.nukkit.event.inventory.FurnaceBurnEvent
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.VariableMap
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.obj.ItemObjectVariable

class FurnaceBurnEventTrigger(subKey: String = "") : EventTrigger(FurnaceBurnEvent::class, subKey) {

    override fun getVariables(event: Event): VariableMap = getVariables(event as FurnaceBurnEvent)

    fun getVariables(event: FurnaceBurnEvent): VariableMap {
        return mapOf(
            "fuel" to ItemObjectVariable(event.fuel)
        )
    }

    override fun getVariablesDummy(): DummyVariableMap {
        return mapOf(
            "fuel" to DummyVariable(DummyVariable.Type.ITEM)
        )
    }
}