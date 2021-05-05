package tokyo.aieuo.mineflow.flowItem.base


import cn.nukkit.level.Position
import tokyo.aieuo.mineflow.exception.InvalidFlowValueException
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.obj.PositionObjectVariable

interface PositionFlowItem {

    var positionVariableNames: MutableMap<String, String>

    fun getPositionVariableName(name: String = ""): String {
        return positionVariableNames[name] ?: ""
    }

    fun setPositionVariableName(position: String, name: String = "") {
        positionVariableNames[name] = position
    }

    fun getPosition(source: FlowItemExecutor, name: String = ""): Position {
        val rawName = getPositionVariableName(name)
        val position = source.replaceVariables(rawName)

        val variable = source.getVariable(position)
        if (variable is PositionObjectVariable<*>) {
            return variable.value
        }

        throw InvalidFlowValueException(Language.get("action.target.not.valid", listOf(
            Language.get("action.target.require.position"),
            rawName
        )))
    }
}