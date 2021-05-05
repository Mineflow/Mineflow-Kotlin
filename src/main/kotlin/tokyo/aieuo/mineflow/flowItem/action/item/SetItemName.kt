package tokyo.aieuo.mineflow.flowItem.action.item

import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.ItemFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ItemVariableDropdown
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable

class SetItemName(item: String = "", var itemName: String = ""): FlowItem(), ItemFlowItem {

    override val id = FlowItemIds.SET_ITEM_NAME

    override val nameTranslationKey = "action.setItemName.name"
    override val detailTranslationKey = "action.setItemName.detail"
    override val detailDefaultReplaces = listOf("item", "name")

    override val category = Category.ITEM

    override var itemVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setItemVariableName(item)
    }

    override fun isDataValid(): Boolean {
        return getItemVariableName() != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getItemVariableName(), itemName))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val name = source.replaceVariables(itemName)

        val item = getItem(source)

        item.customName = name
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            ItemVariableDropdown(variables, getItemVariableName()),
            ExampleInput("@action.createItemVariable.form.name", "aieuo", itemName, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setItemVariableName(contents.getString(0))
        itemName = contents.getString(1)
    }

    override fun serializeContents(): List<Any> {
        return listOf(getItemVariableName(), itemName)
    }

    override fun clone(): SetItemName {
        val item = super.clone() as SetItemName
        item.itemVariableNames = itemVariableNames.toMutableMap()
        return item
    }
}
