package tokyo.aieuo.mineflow.flowItem.action.item

import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.ItemFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleNumberInput
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ItemVariableDropdown
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable

class SetItemCount(item: String = "", var count: String = ""): FlowItem(), ItemFlowItem {

    override val id = FlowItemIds.SET_ITEM_COUNT

    override val nameTranslationKey = "action.setItemCount.name"
    override val detailTranslationKey = "action.setItemCount.detail"
    override val detailDefaultReplaces = listOf("item", "count")

    override val category = Category.ITEM

    override var itemVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setItemVariableName(item)
    }

    override fun isDataValid(): Boolean {
        return getItemVariableName() != "" && count != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getItemVariableName(), count))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val count = source.replaceVariables(count)
        throwIfInvalidNumber(count, 0.0)

        val item = getItem(source)

        item.count = count.toIntOrNull() ?: count.toDouble().toInt()
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            ItemVariableDropdown(variables, getItemVariableName()),
            ExampleNumberInput("@action.createItemVariable.form.count", "64", count, true, 0.0),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setItemVariableName(contents.getString(0))
        count = contents.getString(1)
    }

    override fun serializeContents(): List<Any> {
        return listOf(getItemVariableName(), count)
    }

    override fun clone(): SetItemCount {
        val item = super.clone() as SetItemCount
        item.itemVariableNames = itemVariableNames.toMutableMap()
        return item
    }
}
