package tokyo.aieuo.mineflow.trigger.event

import cn.nukkit.event.Event
import cn.nukkit.event.level.LevelLoadEvent
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.Variable
import tokyo.aieuo.mineflow.variable.obj.WorldObjectVariable

class LevelLoadEventTrigger(subKey: String = ""): EventTrigger(LevelLoadEvent::class, subKey) {

    override fun getVariables(event: Event): Map<String, Variable<Any>> = getVariables(event as LevelLoadEvent)

    fun getVariables(event: LevelLoadEvent): Map<String, Variable<Any>> {
        return mapOf(
            "world" to WorldObjectVariable(event.level)
        )
    }

    override fun getVariablesDummy(): Map<String, DummyVariable<DummyVariable.Type>> {
        return mapOf(
            "world" to DummyVariable(DummyVariable.Type.WORLD)
        )
    }
}