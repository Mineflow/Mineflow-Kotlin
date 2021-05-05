package tokyo.aieuo.mineflow.variable.obj

import cn.nukkit.event.Event
import tokyo.aieuo.mineflow.variable.*

class EventObjectVariable<T: Event>(value: T, showString: String? = null): ObjectVariable<T>(value, showString) {

    override fun getValueFromIndex(index: String): Variable<Any>? {
        return when (index) {
            "name" -> StringVariable(toString())
            "isCanceled" -> BoolVariable(value.isCancelled)
            else -> null
        }
    }

    override fun toString(): String {
        return value.eventName.split("\\").last()
    }


    companion object {
        fun getValuesDummy(): Map<String, DummyVariable<DummyVariable.Type>> {
            return mapOf(
                "name" to DummyVariable(DummyVariable.Type.STRING),
                "isCanceled" to DummyVariable(DummyVariable.Type.BOOLEAN),
            )
        }
    }
}
