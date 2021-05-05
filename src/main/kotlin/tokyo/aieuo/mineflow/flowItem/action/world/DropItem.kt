package tokyo.aieuo.mineflow.flowItem.action.world

import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.ItemFlowItem
import tokyo.aieuo.mineflow.flowItem.base.PositionFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ItemVariableDropdown
import tokyo.aieuo.mineflow.formAPI.element.mineflow.PositionVariableDropdown
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable

class DropItem(position: String = "", item: String = ""): FlowItem(), PositionFlowItem, ItemFlowItem {

    override val id = FlowItemIds.DROP_ITEM

    override val nameTranslationKey = "action.dropItem.name"
    override val detailTranslationKey = "action.dropItem.detail"
    override val detailDefaultReplaces = listOf("position", "item")

    override val category = Category.WORLD

    override var positionVariableNames: MutableMap<String, String> = mutableMapOf()
    override var itemVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setPositionVariableName(position)
        setItemVariableName(item)
    }

    override fun isDataValid(): Boolean {
        return getPositionVariableName() != "" && getItemVariableName() != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getPositionVariableName(), getItemVariableName()))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val position = getPosition(source)

        val item = getItem(source)

        position.level.dropItem(position, item)
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            PositionVariableDropdown(variables, getPositionVariableName()),
            ItemVariableDropdown(variables, getItemVariableName()),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setPositionVariableName(contents.getString(0))
        setItemVariableName(contents.getString(1))
    }

    override fun serializeContents(): List<Any> {
        return listOf(getPositionVariableName(), getItemVariableName())
    }

    override fun clone(): DropItem {
        val item = super.clone() as DropItem
        item.positionVariableNames = positionVariableNames.toMutableMap()
        item.itemVariableNames = itemVariableNames.toMutableMap()
        return item
    }
}