package tokyo.aieuo.mineflow.flowItem.action.world

import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.PositionFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.element.mineflow.PositionVariableDropdown
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.obj.BlockObjectVariable

class GetBlock(position: String = "", var resultName: String = "block"): FlowItem(), PositionFlowItem {

    override val id = FlowItemIds.GET_BLOCK

    override val nameTranslationKey = "action.getBlock.name"
    override val detailTranslationKey = "action.getBlock.detail"
    override val detailDefaultReplaces = listOf("position", "result")

    override val category = Category.WORLD

    override var positionVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setPositionVariableName(position)
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getPositionVariableName(), resultName))
    }

    override fun isDataValid(): Boolean {
        return getPositionVariableName() != "" && resultName != ""
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val position = getPosition(source)
        val result = source.replaceVariables(resultName)

        val block = position.level.getBlock(position)

        val variable = BlockObjectVariable(block)
        source.addVariable(result, variable)
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            PositionVariableDropdown(variables, getPositionVariableName()),
            ExampleInput("@action.form.resultVariableName", "block", resultName, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setPositionVariableName(contents.getString(0))
        resultName = contents.getString(1)
    }

    override fun serializeContents(): List<Any> {
        return listOf(getPositionVariableName(), resultName)
    }

    override fun getAddingVariables(): Map<String, DummyVariable<DummyVariable.Type>> {
        return mapOf(
            resultName to DummyVariable(DummyVariable.Type.BLOCK)
        )
    }

    override fun clone(): GetBlock {
        val item = super.clone() as GetBlock
        item.positionVariableNames = positionVariableNames.toMutableMap()
        return item
    }
}
