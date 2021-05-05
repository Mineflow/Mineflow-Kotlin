package tokyo.aieuo.mineflow.variable

import tokyo.aieuo.mineflow.utils.JsonSerializable

class ListVariable(var value: List<Variable<Any>>, val showString: String? = null): Variable<List<Variable<Any>>>, JsonSerializable {

    override val type = Variable.LIST

//    fun appendValue(variable: Variable<Any>) {
//        value.add(variable)
//    }
//
//    fun setValueAt(key: Int, variable: Variable<Any>) {
//        value.add(key, variable)
//    }
//
//    fun removeValue(variable: Variable<Any>) {
//        value.remove(variable)
//    }

    override fun getValueFromIndex(index: String): Variable<Any>? {
        return value.getOrNull(index.toInt())
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
        value.forEach {
            values.add(it.toString())
        }
        return "[${values.joinToString(",")}]"
    }

    override fun jsonSerialize(): Map<String, Any?> {
        return mapOf(
            "type" to type,
            "value" to value,
        )
    }

    fun toArray(): List<Any> {
        val result = mutableListOf<Any>()
        value.forEach {
            result.add(if (it is ListVariable) it.toArray() else it.toString())
        }
        return result
    }
}