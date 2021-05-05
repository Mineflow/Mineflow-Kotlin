package tokyo.aieuo.mineflow.variable.obj

import cn.nukkit.block.Block
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.NumberVariable
import tokyo.aieuo.mineflow.variable.StringVariable
import tokyo.aieuo.mineflow.variable.Variable

class BlockObjectVariable<T: Block>(value: T, showString: String? = null): PositionObjectVariable<T>(value, showString) {

    override fun getValueFromIndex(index: String): Variable<Any>? {
        super.getValueFromIndex(index).let { if (it !== null) return it }

        return when (index) {
            "name" -> StringVariable(value.name)
            "id" -> NumberVariable(value.id)
            "damage" -> NumberVariable(value.damage)
            else -> null
        }
    }


    companion object {
        fun getValuesDummy(): Map<String, DummyVariable<DummyVariable.Type>> {
            return PositionObjectVariable.getValuesDummy().plus(mapOf(
                "name" to DummyVariable(DummyVariable.Type.STRING),
                "id" to DummyVariable(DummyVariable.Type.NUMBER),
                "damage" to DummyVariable(DummyVariable.Type.NUMBER),
            ))
        }
    }
}