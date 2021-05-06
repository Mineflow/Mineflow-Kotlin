package tokyo.aieuo.mineflow.flowItem.base

import cn.nukkit.math.Vector3
import tokyo.aieuo.mineflow.exception.InvalidFlowValueException
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.obj.Vector3ObjectVariable

interface Vector3FlowItem {

    var vector3VariableNames: MutableMap<String, String>

    fun getVector3VariableName(name: String = ""): String {
        return vector3VariableNames[name] ?: ""
    }

    fun setVector3VariableName(vector3: String, name: String = "") {
        vector3VariableNames[name] = vector3
    }

    fun getVector3(source: FlowItemExecutor, name: String = ""): Vector3 {
        val rawName = getVector3VariableName(name)
        val vector3 = source.replaceVariables(rawName)

        val variable = source.getVariable(vector3)
        if (variable is Vector3ObjectVariable<*>) {
            return variable.value
        }

        throw InvalidFlowValueException(
            Language.get(
                "action.target.not.valid", listOf(
                    Language.get("action.target.require.vector3"),
                    rawName
                )
            )
        )
    }
}
