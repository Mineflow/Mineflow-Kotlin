package tokyo.aieuo.mineflow.flowItem.condition

import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.base.ItemFlowItem
import tokyo.aieuo.mineflow.flowItem.base.PlayerFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ItemVariableDropdown
import tokyo.aieuo.mineflow.formAPI.element.mineflow.PlayerVariableDropdown
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable

@Suppress("LeakingThis")
abstract class TypeItem(player: String = "", item: String = ""): FlowItem(), Condition, PlayerFlowItem, ItemFlowItem {

    override val detailDefaultReplaces = listOf("player", "item")

    override val category = Category.INVENTORY

    override var playerVariableNames: MutableMap<String, String> = mutableMapOf()
    override var itemVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setPlayerVariableName(player)
        setItemVariableName(item)
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getPlayerVariableName(), getItemVariableName()))
    }

    override fun isDataValid(): Boolean {
        return getPlayerVariableName() != "" && getItemVariableName() != ""
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            PlayerVariableDropdown(variables, getPlayerVariableName()),
            ItemVariableDropdown(variables, getItemVariableName()),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setPlayerVariableName(contents.getString(0))
        setItemVariableName(contents.getString(1))
    }

    override fun serializeContents(): List<Any> {
        return listOf(getPlayerVariableName(), getItemVariableName())
    }

    override fun clone(): TypeItem {
        val item = super.clone() as TypeItem
        item.playerVariableNames = playerVariableNames.toMutableMap()
        item.itemVariableNames = itemVariableNames.toMutableMap()
        return item
    }
}
