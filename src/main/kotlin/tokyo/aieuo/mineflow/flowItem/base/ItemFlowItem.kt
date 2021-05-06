package tokyo.aieuo.mineflow.flowItem.base


import cn.nukkit.item.Item
import tokyo.aieuo.mineflow.exception.InvalidFlowValueException
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.obj.ItemObjectVariable

interface ItemFlowItem {

    var itemVariableNames: MutableMap<String, String>

    fun getItemVariableName(name: String = ""): String {
        return itemVariableNames[name] ?: ""
    }

    fun setItemVariableName(item: String, name: String = "") {
        itemVariableNames[name] = item
    }

    fun getItem(source: FlowItemExecutor, name: String = ""): Item {
        val rawName = getItemVariableName(name)
        val item = source.replaceVariables(rawName)

        val variable = source.getVariable(item)
        if (variable is ItemObjectVariable) {
            return variable.value
        }

        throw InvalidFlowValueException(
            Language.get(
                "action.target.not.valid", listOf(
                    Language.get("action.target.require.item"),
                    rawName
                )
            )
        )
    }
}