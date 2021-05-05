package tokyo.aieuo.mineflow.variable.obj

import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.StringVariable
import tokyo.aieuo.mineflow.variable.Variable
import cn.nukkit.Player
import tokyo.aieuo.mineflow.variable.NumberVariable

class PlayerObjectVariable<T: Player>(value: T, showString: String? = null): HumanObjectVariable<T>(value, showString) {

    override fun getValueFromIndex(index: String): Variable<Any>? {
        super.getValueFromIndex(index).let { if (it !== null) return it }

        return when (index) {
            "name" -> StringVariable(value.name)
            "food" -> NumberVariable(value.foodData.level)
            else -> null
        }
    }


    companion object {
        fun getValuesDummy(): Map<String, DummyVariable<DummyVariable.Type>> {
            return HumanObjectVariable.getValuesDummy().plus(mapOf(
                "name" to DummyVariable(DummyVariable.Type.STRING),
            ))
        }
    }
}