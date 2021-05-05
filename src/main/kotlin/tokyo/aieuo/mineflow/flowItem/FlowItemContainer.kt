package tokyo.aieuo.mineflow.flowItem

import tokyo.aieuo.mineflow.variable.DummyVariable

interface FlowItemContainer {

    companion object {
        const val ACTION = "action"
        const val CONDITION = "condition"
    }

    var items: MutableMap<String, MutableList<FlowItem>>

    fun getContainerName(): String

    fun addItem(action: FlowItem, name: String) {
        if (!items.containsKey(name)) items[name] = mutableListOf()
        items[name]?.add(action)
    }

    fun setItems(actions: MutableList<FlowItem>, name: String) {
        items[name] = actions
    }

    fun pushItem(index: Int, action: FlowItem, name: String) {
        if (!items.containsKey(name)) items[name] = mutableListOf()
        items[name]?.add(index, action)
    }

    fun getItem(index: Int, name: String): FlowItem? {
        return items[name]?.get(index)
    }

    fun removeItem(index: Int, name: String) {
        items[name]?.removeAt(index)
    }

    fun getItems(name: String): MutableList<FlowItem> {
        return items[name] ?: mutableListOf()
    }

    fun getActions() = getItems(ACTION)

    fun getConditions() = getItems(CONDITION)

    fun getAddingVariablesBefore(flowItem: FlowItem, containers: MutableList<FlowItemContainer>, type: String): Map<String, DummyVariable<DummyVariable.Type>> {
        val variables = mutableMapOf<String, DummyVariable<DummyVariable.Type>>()

        val target = containers.removeFirstOrNull()?.also {
            if (it is FlowItem) variables.putAll(it.getAddingVariables())
            variables.putAll(it.getAddingVariablesBefore(flowItem, containers, type))
        } ?: flowItem

        for (item in getItems(type)) {
            if (item === target) break
            variables.putAll(item.getAddingVariables())
        }
        return variables
    }
}