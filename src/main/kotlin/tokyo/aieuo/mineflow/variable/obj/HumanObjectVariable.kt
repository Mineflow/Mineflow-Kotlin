package tokyo.aieuo.mineflow.variable.obj

import cn.nukkit.entity.EntityHuman
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.Variable

open class HumanObjectVariable<T : EntityHuman>(value: T, showString: String? = null) :
    EntityObjectVariable<T>(value, showString) {

    override fun getValueFromIndex(index: String): Variable<Any>? {
        super.getValueFromIndex(index).let { if (it !== null) return it }

        return when (index) {
            "hand" -> ItemObjectVariable(value.inventory.itemInHand)
            else -> null
        }
    }

    override fun toString(): String {
        return value.name
    }

    companion object {
        fun getValuesDummy(): DummyVariableMap {
            return EntityObjectVariable.getValuesDummy().plus(
                mapOf(
                    "hand" to DummyVariable(DummyVariable.Type.ITEM),
                    "food" to DummyVariable(DummyVariable.Type.NUMBER),
                )
            )
        }
    }
}