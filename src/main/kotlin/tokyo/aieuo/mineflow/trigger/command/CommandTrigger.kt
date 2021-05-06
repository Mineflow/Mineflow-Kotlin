package tokyo.aieuo.mineflow.trigger.command

import tokyo.aieuo.mineflow.trigger.Trigger
import tokyo.aieuo.mineflow.trigger.Triggers
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.utils.VariableMap
import tokyo.aieuo.mineflow.variable.DefaultVariables
import tokyo.aieuo.mineflow.variable.DummyVariable

class CommandTrigger(key: String, subKey: String = "") : Trigger(Triggers.COMMAND, key, subKey) {

    companion object {
        fun create(key: String, subKey: String = ""): CommandTrigger {
            return CommandTrigger(key, subKey)
        }
    }

    fun getVariables(command: String): VariableMap {
        return DefaultVariables.getCommandVariables(command)
    }

    override fun getVariablesDummy(): DummyVariableMap {
        return mapOf(
            "cmd" to DummyVariable(DummyVariable.Type.STRING),
            "args" to DummyVariable(DummyVariable.Type.LIST, DummyVariable.Type.STRING),
        )
    }

    override fun toString(): String {
        return Language.get("trigger.command.string", listOf(subKey))
    }
}