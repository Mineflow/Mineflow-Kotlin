package tokyo.aieuo.mineflow.variable.obj

import cn.nukkit.entity.Entity
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.NumberVariable
import tokyo.aieuo.mineflow.variable.StringVariable
import tokyo.aieuo.mineflow.variable.Variable

open class EntityObjectVariable<T: Entity>(value: T, showString: String? = null): PositionObjectVariable<T>(value, showString) {

    override fun getValueFromIndex(index: String): Variable<Any>? {
        super.getValueFromIndex(index).let { if (it !== null) return it }

        return when (index) {
            "id" -> NumberVariable(value.id)
            "nameTag" -> StringVariable(value.nameTag)
            "health" -> NumberVariable(value.health)
            "maxHealth" -> NumberVariable(value.maxHealth)
            "yaw" -> NumberVariable(value.yaw)
            "pitch" -> NumberVariable(value.pitch)
            "direction" -> NumberVariable((value.direction.horizontalIndex + 1) % 4)
            else -> null
        }
    }

    companion object {
        fun getValuesDummy(): Map<String, DummyVariable<DummyVariable.Type>> {
            return PositionObjectVariable.getValuesDummy().plus(mapOf(
                "id" to DummyVariable(DummyVariable.Type.NUMBER),
                "nameTag" to DummyVariable(DummyVariable.Type.STRING),
                "health" to DummyVariable(DummyVariable.Type.NUMBER),
                "maxHealth" to DummyVariable(DummyVariable.Type.NUMBER),
                "yaw" to DummyVariable(DummyVariable.Type.NUMBER),
                "pitch" to DummyVariable(DummyVariable.Type.NUMBER),
                "direction" to DummyVariable(DummyVariable.Type.NUMBER),
            ))
        }
    }
}