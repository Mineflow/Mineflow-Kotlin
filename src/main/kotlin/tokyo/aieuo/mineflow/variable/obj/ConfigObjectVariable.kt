package tokyo.aieuo.mineflow.variable.obj

import cn.nukkit.utils.Config
import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.is_numeric
import tokyo.aieuo.mineflow.variable.*

class ConfigObjectVariable<T : Config>(value: T, showString: String? = null) : ObjectVariable<T>(value, showString) {

    override fun getValueFromIndex(index: String): Variable<Any>? {
        val data = value.get(index)

        if (data is String && is_numeric(data)) return NumberVariable(data.toDouble())
        if (data is String) return StringVariable(data)
        if (data is Boolean) return BoolVariable(data)
        if (data is List<*>) return ListVariable(Main.variableHelper.toVariableArray(data))
        if (data is Map<*, *>) return MapVariable(Main.variableHelper.toVariableArray(data))
        return null
    }

    override fun toString(): String {
        return "ConfigVariable"
    }

    companion object {
        fun getValuesDummy(): DummyVariableMap {
            return mapOf()
        }
    }
}