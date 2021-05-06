package tokyo.aieuo.mineflow.flowItem.base


import cn.nukkit.utils.Config
import tokyo.aieuo.mineflow.exception.InvalidFlowValueException
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.obj.ConfigObjectVariable

interface ConfigFileFlowItem {

    var configVariableNames: MutableMap<String, String>

    fun getConfigVariableName(name: String = ""): String {
        return configVariableNames[name] ?: ""
    }

    fun setConfigVariableName(config: String, name: String = "") {
        configVariableNames[name] = config
    }

    fun getConfig(source: FlowItemExecutor, name: String = ""): Config {
        val rawName = getConfigVariableName(name)
        val config = source.replaceVariables(rawName)

        val variable = source.getVariable(config)
        if (variable is ConfigObjectVariable<*>) {
            return variable.value
        }

        throw InvalidFlowValueException(
            Language.get(
                "action.target.not.valid", listOf(
                    Language.get("action.target.require.config"),
                    rawName
                )
            )
        )
    }
}
