package tokyo.aieuo.mineflow.trigger.time

import tokyo.aieuo.mineflow.trigger.Trigger
import tokyo.aieuo.mineflow.trigger.Triggers
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.utils.VariableMap
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.NumberVariable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TimeTrigger(hour: String, minutes: String = "") : Trigger(Triggers.TIME, hour, minutes) {

    companion object {
        fun create(hour: String, minutes: String = ""): TimeTrigger {
            return TimeTrigger(hour, minutes)
        }
    }

    fun getVariables(): VariableMap {
        val date = LocalDateTime.now()
        return mapOf(
            "hour" to NumberVariable(date.format(DateTimeFormatter.ofPattern("H")).toInt()),
            "minutes" to NumberVariable(date.format(DateTimeFormatter.ofPattern("i")).toInt()),
            "seconds" to NumberVariable(date.format(DateTimeFormatter.ofPattern("s")).toInt()),
        )
    }

    override fun getVariablesDummy(): DummyVariableMap {
        return mapOf(
            "hour" to DummyVariable(DummyVariable.Type.NUMBER),
            "minutes" to DummyVariable(DummyVariable.Type.NUMBER),
            "seconds" to DummyVariable(DummyVariable.Type.NUMBER),
        )
    }

    override fun toString(): String {
        return Language.get("trigger.time.string", listOf(key, subKey))
    }
}