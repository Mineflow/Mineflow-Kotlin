package tokyo.aieuo.mineflow.variable.obj

import cn.nukkit.level.Position
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.Variable

open class PositionObjectVariable<T : Position>(value: T, showString: String? = null) :
    Vector3ObjectVariable<T>(value, showString) {

    override fun getValueFromIndex(index: String): Variable<Any>? {
        super.getValueFromIndex(index).let { if (it !== null) return it }

        return when (index) {
            "position" -> PositionObjectVariable(value)
            "world" -> WorldObjectVariable(value.level, value.level.folderName)
            else -> null
        }
    }

    override fun toString(): String {
        return "${super.toString()},${value.level.folderName}"
    }

    companion object {
        fun getValuesDummy(): DummyVariableMap {
            return Vector3ObjectVariable.getValuesDummy() + mapOf(
                "world" to DummyVariable(DummyVariable.Type.WORLD),
            )
        }
    }
}