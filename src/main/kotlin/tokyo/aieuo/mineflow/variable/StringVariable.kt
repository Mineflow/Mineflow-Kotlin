package tokyo.aieuo.mineflow.variable

import tokyo.aieuo.mineflow.utils.JsonSerializable

class StringVariable(var value: String): Variable<String>, JsonSerializable {

    override val type = Variable.STRING

    override operator fun plus(variable: Variable<Any>): StringVariable {
        return StringVariable(value + variable.toString())
    }

    override operator fun minus(variable: Variable<Any>): StringVariable {
        return StringVariable(value.replace(variable.toString(), ""))
    }

    override operator fun times(variable: Variable<Any>): StringVariable {
        if (variable !is NumberVariable) throw UnsupportedOperationException()

        return StringVariable(value.repeat(variable.value.toInt()))
    }

    override fun jsonSerialize(): Map<String, Any?> {
        return mapOf(
            "type" to type,
            "value" to value,
        )
    }

    override fun toString(): String {
        return value
    }
}