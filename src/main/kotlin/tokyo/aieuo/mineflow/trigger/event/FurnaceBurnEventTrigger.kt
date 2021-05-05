package tokyo.aieuo.mineflow.trigger.event

import cn.nukkit.event.Event
import cn.nukkit.event.inventory.FurnaceBurnEvent
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.Variable
import tokyo.aieuo.mineflow.variable.obj.ItemObjectVariable

class FurnaceBurnEventTrigger(subKey: String = ""): EventTrigger(FurnaceBurnEvent::class, subKey) {

    override fun getVariables(event: Event): Map<String, Variable<Any>> = getVariables(event as FurnaceBurnEvent)

    fun getVariables(event: FurnaceBurnEvent): Map<String, Variable<Any>> {
        return mapOf(
            "fuel" to ItemObjectVariable(event.fuel)
        )
    }

    override fun getVariablesDummy(): Map<String, DummyVariable<DummyVariable.Type>> {
        return mapOf(
            "fuel" to DummyVariable(DummyVariable.Type.ITEM)
        )
    }
}