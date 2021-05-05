package tokyo.aieuo.mineflow.trigger

import tokyo.aieuo.mineflow.utils.JsonSerializable
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable

open class Trigger(val type: String, var key: String, var subKey: String = ""): JsonSerializable {

    open fun getVariablesDummy(): Map<String, DummyVariable<DummyVariable.Type>> {
        return mapOf()
    }

    override fun jsonSerialize(): Map<String, Any?> {
        return mapOf(
            "type" to type,
            "key" to key,
            "subKey" to subKey,
        )
    }

    override fun toString(): String {
        return "trigger.type.$type".let {
            (if (Language.exists(it)) Language.get(it) else type) + ": key, subKey"
        }
    }
}