package tokyo.aieuo.mineflow.variable

import tokyo.aieuo.mineflow.exception.UnsupportedCalculationException

interface Variable<out T> {

    val type: Int

    companion object {
        const val DUMMY = -1
        const val STRING = 0
        const val NUMBER = 1
        const val LIST = 2
        const val MAP = 3
        const val OBJECT = 4
        const val BOOLEAN = 5

        @Suppress("UNCHECKED_CAST")
        fun create(value: Any, type: Int): Variable<Any>? {
            return when (type) {
                STRING -> StringVariable(if (value is String) value else value.toString())
                NUMBER -> NumberVariable(if (value is Number) value else value.toString().toDoubleOrNull() ?: return null)
                LIST -> ListVariable((if (value is Map<*, *>) value.values.toList() else value) as List<Variable<Any>>)
                MAP -> MapVariable((if (value is List<*>) value.withIndex().map { (k, v) -> k to v }.toMap() else value) as Map<String, Variable<Any>>)
                else -> null
            }
        }

        @Suppress("UNCHECKED_CAST")
        fun fromArray(data: Map<String, Any>): Variable<Any>? {
            if (!data.containsKey("value") || !data.containsKey("type")) return null

            val type = (data["type"] as Double).toInt()
            return when (val value = data["value"] ?: return null) {
                is List<*> -> {
                    val values = mutableListOf<Variable<Any>>()
                    for (v in value) {
                        values.add(fromArray(v as? Map<String, Any> ?: continue) ?: continue)
                    }
                    create(values, type)
                }
                is Map<*, *> -> {
                    val values = mutableMapOf<String, Variable<Any>>()
                    for ((k, v) in value) {
                        if (k !is String) continue

                        values[k] = fromArray(v as? Map<String, Any> ?: continue) ?: continue
                    }
                    create(values, type)
                }
                else -> create(value, type)
            }
        }
    }

    fun getValueFromIndex(index: String): Variable<Any>? {
        return null
    }

    fun callMethod(name: String, parameters: List<String>): Variable<Any>? {
        return null
    }

    override fun toString(): String

    fun toStringVariable(): StringVariable {
        return StringVariable(toString())
    }

    operator fun plus(variable: Variable<Any>): Variable<Any> {
        throw UnsupportedCalculationException()
    }

    operator fun minus(variable: Variable<Any>): Variable<Any> {
        throw UnsupportedCalculationException()
    }

    operator fun times(variable: Variable<Any>): Variable<Any> {
        throw UnsupportedCalculationException()
    }

    operator fun div(variable: Variable<Any>): Variable<Any> {
        throw UnsupportedCalculationException()
    }
}