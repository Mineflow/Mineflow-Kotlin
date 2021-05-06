package tokyo.aieuo.mineflow.variable.obj

import cn.nukkit.Player
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.NumberVariable
import tokyo.aieuo.mineflow.variable.StringVariable
import tokyo.aieuo.mineflow.variable.Variable

class PlayerObjectVariable<T : Player>(value: T, showString: String? = null) :
    HumanObjectVariable<T>(value, showString) {

    override fun getValueFromIndex(index: String): Variable<Any>? {
        super.getValueFromIndex(index).let { if (it !== null) return it }

        return when (index) {
            "name" -> StringVariable(value.name)
            "food" -> NumberVariable(value.foodData.level)
            else -> null
        }
    }


    companion object {
        fun getValuesDummy(): DummyVariableMap {
            return HumanObjectVariable.getValuesDummy() + mapOf(
                "name" to DummyVariable(DummyVariable.Type.STRING),
            )
        }
    }
}