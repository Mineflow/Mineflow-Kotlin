package tokyo.aieuo.mineflow.flowItem.action.player

import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.PlayerFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleNumberInput
import tokyo.aieuo.mineflow.formAPI.element.mineflow.PlayerVariableDropdown
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.obj.BlockObjectVariable

class GetTargetBlock(player: String = "", var max: String = "100", var resultName: String = "block"): FlowItem(), PlayerFlowItem {

    override val id = FlowItemIds.GET_TARGET_BLOCK

    override val nameTranslationKey = "action.getTargetBlock.name"
    override val detailTranslationKey = "action.getTargetBlock.detail"
    override val detailDefaultReplaces = listOf("player", "maxDistance", "result")

    override val category = Category.PLAYER

    override var playerVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setPlayerVariableName(player)
    }

    override fun isDataValid(): Boolean {
        return getPlayerVariableName() != "" && max != "" && resultName != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getPlayerVariableName(), max, resultName))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val max = source.replaceVariables(max)
        throwIfInvalidNumber(max, 1.0)
        val result = source.replaceVariables(resultName)

        val player = getPlayer(source)
        throwIfInvalidPlayer(player)

        val block = player.getTargetBlock(max.toInt())
        source.addVariable(result, BlockObjectVariable(block))
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            PlayerVariableDropdown(variables, getPlayerVariableName()),
            ExampleNumberInput("@action.getTargetBlock.form.max", "100", max, true),
            ExampleInput("@action.form.resultVariableName", "block", resultName, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setPlayerVariableName(contents.getString(0))
        max = contents.getString(1)
        resultName = contents.getString(2)
    }

    override fun serializeContents(): List<Any> {
        return listOf(getPlayerVariableName(), max, resultName)
    }

    override fun getAddingVariables(): Map<String, DummyVariable<DummyVariable.Type>> {
        return mapOf(
            resultName to DummyVariable(DummyVariable.Type.BLOCK)
        )
    }

    override fun clone(): GetTargetBlock {
        val item = super.clone() as GetTargetBlock
        item.playerVariableNames = playerVariableNames.toMutableMap()
        return item
    }
}