package tokyo.aieuo.mineflow.trigger.event

import cn.nukkit.event.Event
import cn.nukkit.event.level.LevelLoadEvent
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.VariableMap
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.obj.WorldObjectVariable

class LevelLoadEventTrigger(subKey: String = "") : EventTrigger(LevelLoadEvent::class, subKey) {

    override fun getVariables(event: Event): VariableMap = getVariables(event as LevelLoadEvent)

    fun getVariables(event: LevelLoadEvent): VariableMap {
        return mapOf(
            "world" to WorldObjectVariable(event.level)
        )
    }

    override fun getVariablesDummy(): DummyVariableMap {
        return mapOf(
            "world" to DummyVariable(DummyVariable.Type.WORLD)
        )
    }
}