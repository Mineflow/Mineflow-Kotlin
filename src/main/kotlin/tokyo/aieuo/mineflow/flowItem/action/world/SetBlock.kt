package tokyo.aieuo.mineflow.flowItem.action.world

import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.BlockFlowItem
import tokyo.aieuo.mineflow.flowItem.base.PositionFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.BlockVariableDropdown
import tokyo.aieuo.mineflow.formAPI.element.mineflow.PositionVariableDropdown
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable

class SetBlock(position: String = "", block: String = ""): FlowItem(), PositionFlowItem, BlockFlowItem {

    override val id = FlowItemIds.SET_BLOCK

    override val nameTranslationKey = "action.setBlock.name"
    override val detailTranslationKey = "action.setBlock.detail"
    override val detailDefaultReplaces = listOf("position", "block")

    override val category = Category.WORLD

    override var positionVariableNames: MutableMap<String, String> = mutableMapOf()
    override var blockVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setPositionVariableName(position)
        setBlockVariableName(block)
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getPositionVariableName(), getBlockVariableName()))
    }

    override fun isDataValid(): Boolean {
        return getPositionVariableName() != "" && getBlockVariableName() != ""
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val position = getPosition(source)

        val block = getBlock(source)

        position.level.setBlock(position, block)
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            PositionVariableDropdown(variables, getPositionVariableName()),
            BlockVariableDropdown(variables, getBlockVariableName()),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setPositionVariableName(contents.getString(0))
        setBlockVariableName(contents.getString(1))
    }

    override fun serializeContents(): List<Any> {
        return listOf(getPositionVariableName(), getBlockVariableName())
    }

    override fun clone(): SetBlock {
        val item = super.clone() as SetBlock
        item.positionVariableNames = positionVariableNames.toMutableMap()
        item.blockVariableNames = blockVariableNames.toMutableMap()
        return item
    }
}
