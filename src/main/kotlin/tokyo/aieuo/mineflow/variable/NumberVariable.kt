package tokyo.aieuo.mineflow.variable

import tokyo.aieuo.mineflow.utils.JsonSerializable

class NumberVariable(var value: Number) : Variable<Number>, JsonSerializable {

    override val type = Variable.NUMBER

    fun zero(): NumberVariable {
        return NumberVariable(0f)
    }

    override operator fun plus(variable: Variable<Any>): NumberVariable {
        if (variable !is NumberVariable) throw UnsupportedOperationException()

        return NumberVariable(value.toDouble() + variable.value.toDouble())
    }

    override operator fun minus(variable: Variable<Any>): NumberVariable {
        if (variable !is NumberVariable) throw UnsupportedOperationException()

        return NumberVariable(value.toDouble() - variable.value.toDouble())
    }

    override operator fun times(variable: Variable<Any>): NumberVariable {
        if (variable !is NumberVariable) throw UnsupportedOperationException()

        return NumberVariable(value.toDouble() * variable.value.toDouble())
    }

    override operator fun div(variable: Variable<Any>): NumberVariable {
        if (variable !is NumberVariable) throw UnsupportedOperationException()

        return NumberVariable(value.toDouble() / variable.value.toDouble())
    }

    override fun toString(): String {
        return value.toString()
    }

    override fun jsonSerialize(): Map<String, Any?> {
        return mapOf(
            "type" to type,
            "value" to value,
        )
    }
}