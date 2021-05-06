package tokyo.aieuo.mineflow.trigger.event

import cn.nukkit.entity.Entity
import cn.nukkit.event.Event
import cn.nukkit.event.entity.EntityEvent
import cn.nukkit.event.player.PlayerEvent
import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.trigger.Trigger
import tokyo.aieuo.mineflow.trigger.Triggers
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.utils.VariableMap
import kotlin.reflect.KClass

open class EventTrigger(key: String, subKey: String = "") : Trigger(Triggers.EVENT, key, subKey) {

    var enabled = true

    companion object {
        fun create(key: String, subKey: String = ""): EventTrigger {
            return (Main.eventManager.getTrigger(key) ?: EventTrigger(key, subKey))
        }
    }

    constructor(event: KClass<out Event>, subKey: String = "") : this(event.java, subKey)

    constructor(event: Class<out Event>, subKey: String = "") : this(event.name, subKey)

    constructor(event: Event, subKey: String = "") : this(event.javaClass, subKey)

    open fun getTargetEntity(event: Event): Entity? {
        return when (event) {
            is PlayerEvent -> event.player
            is EntityEvent -> event.entity
            else -> null
        }
    }

    open fun getVariables(event: Event): VariableMap {
        return mapOf()
    }

    override fun toString(): String {
        return if (Language.exists("trigger.event.${key}")) Language.get("trigger.event.${key}") else key
    }
}