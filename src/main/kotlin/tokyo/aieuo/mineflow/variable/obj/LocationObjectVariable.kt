package tokyo.aieuo.mineflow.variable.obj

import cn.nukkit.level.Location
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.NumberVariable
import tokyo.aieuo.mineflow.variable.Variable

class LocationObjectVariable<T : Location>(value: T, showString: String? = null) :
    PositionObjectVariable<T>(value, showString) {

    override fun getValueFromIndex(index: String): Variable<Any>? {
        super.getValueFromIndex(index).let { if (it !== null) return it }

        return when (index) {
            "yaw" -> NumberVariable(value.yaw)
            "pitch" -> NumberVariable(value.pitch)
            else -> null
        }
    }

    override fun toString(): String {
        return "${super.toString()} (${value.yaw},${value.pitch})"
    }

    companion object {
        fun getValuesDummy(): DummyVariableMap {
            return PositionObjectVariable.getValuesDummy().plus(
                mapOf(
                    "yaw" to DummyVariable(DummyVariable.Type.NUMBER),
                    "pitch" to DummyVariable(DummyVariable.Type.NUMBER),
                )
            )
        }
    }
}