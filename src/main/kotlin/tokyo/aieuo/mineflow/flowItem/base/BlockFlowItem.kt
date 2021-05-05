package tokyo.aieuo.mineflow.flowItem.base

import cn.nukkit.block.Block
import tokyo.aieuo.mineflow.exception.InvalidFlowValueException
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.obj.BlockObjectVariable


interface BlockFlowItem {

    var blockVariableNames: MutableMap<String, String>

    fun getBlockVariableName(name: String = ""): String {
        return blockVariableNames[name] ?: ""
    }

    fun setBlockVariableName(block: String, name: String = "") {
        blockVariableNames[name] = block
    }

    fun getBlock(source: FlowItemExecutor, name: String = ""): Block {
        val rawName = getBlockVariableName(name)
        val block = source.replaceVariables(rawName)

        val variable = source.getVariable(block)
        if (variable is BlockObjectVariable<*>) {
            return variable.value
        }

        throw InvalidFlowValueException(Language.get("action.target.not.valid", listOf(
            Language.get("action.target.require.block"),
            rawName
        )))
    }
}
