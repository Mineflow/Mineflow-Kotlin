package tokyo.aieuo.mineflow.flowItem.action.inventory

import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.ItemFlowItem
import tokyo.aieuo.mineflow.flowItem.base.PlayerFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleNumberInput
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ItemVariableDropdown
import tokyo.aieuo.mineflow.formAPI.element.mineflow.PlayerVariableDropdown
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language

class SetItem(player: String = "", item: String = "", var index: String = "") :
    FlowItem(), PlayerFlowItem, ItemFlowItem {

    override val id = FlowItemIds.SET_ITEM

    override val nameTranslationKey = "action.setItem.name"
    override val detailTranslationKey = "action.setItem.detail"
    override val detailDefaultReplaces = listOf("player", "item", "index")

    override val category = Category.INVENTORY

    override var playerVariableNames: MutableMap<String, String> = mutableMapOf()
    override var itemVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setPlayerVariableName(player)
        setItemVariableName(item)
    }

    override fun isDataValid(): Boolean {
        return getPlayerVariableName() != "" && getItemVariableName() != "" && index != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getPlayerVariableName(), getItemVariableName(), index))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val index = source.replaceVariables(index)

        throwIfInvalidNumber(index, 0.0)

        val player = getPlayer(source)
        throwIfInvalidPlayer(player)

        val item = getItem(source)

        player.inventory.setItem(index.toInt(), item)
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            PlayerVariableDropdown(variables, getPlayerVariableName()),
            ItemVariableDropdown(variables, getItemVariableName()),
            ExampleNumberInput("@action.setItem.form.index", "0", index, true, 0.0),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setPlayerVariableName(contents.getString(0))
        setItemVariableName(contents.getString(1))
        index = contents.getString(2)
    }

    override fun serializeContents(): List<Any> {
        return listOf(getPlayerVariableName(), getItemVariableName(), index)
    }

    override fun clone(): SetItem {
        val item = super.clone() as SetItem
        item.playerVariableNames = playerVariableNames.toMutableMap()
        item.itemVariableNames = itemVariableNames.toMutableMap()
        return item
    }
}