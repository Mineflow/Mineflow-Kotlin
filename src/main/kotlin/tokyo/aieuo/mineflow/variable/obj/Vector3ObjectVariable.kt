package tokyo.aieuo.mineflow.variable.obj

import cn.nukkit.math.Vector3
import tokyo.aieuo.mineflow.variable.*

open class Vector3ObjectVariable<T: Vector3>(value: T, showString: String? = null): ObjectVariable<T>(value, showString) {

    override fun getValueFromIndex(index: String): Variable<Any>? {
        return when (index) {
            "x" -> NumberVariable(value.x)
            "y" -> NumberVariable(value.y)
            "z" -> NumberVariable(value.z)
            "xyz" -> StringVariable("${value.x},${value.y},${value.z}")
            else -> null
        }
    }

    override fun toString(): String {
        return "${value.x},${value.y},${value.z}"
    }

    companion object {
        fun getValuesDummy(): Map<String, DummyVariable<DummyVariable.Type>> {
            return mapOf(
                "x" to DummyVariable(DummyVariable.Type.NUMBER),
                "y" to DummyVariable(DummyVariable.Type.NUMBER),
                "z" to DummyVariable(DummyVariable.Type.NUMBER),
                "xyz" to DummyVariable(DummyVariable.Type.STRING),
            )
        }
    }
}