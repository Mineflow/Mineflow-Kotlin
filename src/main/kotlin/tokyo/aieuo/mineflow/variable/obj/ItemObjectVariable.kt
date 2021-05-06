package tokyo.aieuo.mineflow.variable.obj

import cn.nukkit.item.Item
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.variable.*

class ItemObjectVariable(value: Item, showString: String? = null) : ObjectVariable<Item>(value, showString) {

    override fun getValueFromIndex(index: String): Variable<Any>? {
        return when (index) {
            "name" -> StringVariable(value.name)
            "id" -> NumberVariable(value.id)
            "damage" -> NumberVariable(value.damage)
            "count" -> NumberVariable(value.count)
            "lore" -> ListVariable(value.lore.map { StringVariable(it) }.toMutableList())
            else -> null
        }
    }

    override fun toString(): String {
        return "Item[${value.name}] (${value.id}:${value.damage})x${value.count}"
    }

    companion object {
        fun getValuesDummy(): DummyVariableMap {
            return mapOf(
                "name" to DummyVariable(DummyVariable.Type.STRING),
                "id" to DummyVariable(DummyVariable.Type.NUMBER),
                "damage" to DummyVariable(DummyVariable.Type.NUMBER),
                "count" to DummyVariable(DummyVariable.Type.NUMBER),
                "lore" to DummyVariable(DummyVariable.Type.LIST, DummyVariable.Type.STRING),
            )
        }
    }
}