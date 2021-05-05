package tokyo.aieuo.mineflow.variable

import tokyo.aieuo.mineflow.utils.JsonSerializable

class MapVariable(var value: Map<String, Variable<Any>>, val showString: String? = null)
    : Variable<Map<String, Variable<Any>>>, JsonSerializable {

    override val type = Variable.MAP

    override fun getValueFromIndex(index: String): Variable<Any>? {
        return value[index]
    }

    override fun callMethod(name: String, parameters: List<String>): Variable<Any>? {
        return when (name) {
            "count" -> NumberVariable(getCount())
            else -> null
        }
    }

    fun getCount(): Int {
        return value.size
    }

    override fun toString(): String {
        if (!showString.isNullOrEmpty()) return showString

        val values = mutableListOf<String>()
        value.forEach { (k, v) ->
            values.add("$k:$v")
        }
        return "<${values.joinToString(",")}>"
    }

    override fun jsonSerialize(): Map<String, Any?> {
        return mapOf(
            "type" to type,
            "value" to value,
        )
    }

    fun toArray(): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        value.forEach { (k, v) ->
            result[k] = if (v is ListVariable) v.toArray() else v.toString()
        }
        return result
    }
}