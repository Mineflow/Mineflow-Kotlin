package tokyo.aieuo.mineflow.trigger.custom

import tokyo.aieuo.mineflow.trigger.Trigger
import tokyo.aieuo.mineflow.trigger.Triggers
import tokyo.aieuo.mineflow.utils.Language

class CustomTrigger(key: String, subKey: String = ""): Trigger(Triggers.CUSTOM, key, subKey) {

    companion object {
        fun create(key: String, subKey: String = ""): CustomTrigger {
            return CustomTrigger(key, subKey)
        }
    }

    override fun toString(): String {
        return Language.get("trigger.custom.string", listOf(key))
    }
}