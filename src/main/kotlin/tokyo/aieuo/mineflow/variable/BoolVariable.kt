package tokyo.aieuo.mineflow.variable

import tokyo.aieuo.mineflow.utils.JsonSerializable

class BoolVariable(val value: Boolean) : Variable<Boolean>, JsonSerializable {

    override val type = Variable.BOOLEAN

    override fun toString(): String {
        return if (value) "true" else "false"
    }

    override fun jsonSerialize(): Map<String, Any?> {
        return mapOf(
            "type" to type,
            "value" to value,
        )
    }
}