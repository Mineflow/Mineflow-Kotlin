package tokyo.aieuo.mineflow.flowItem.action.block

import cn.nukkit.item.Item
import tokyo.aieuo.mineflow.exception.InvalidFlowValueException
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.obj.BlockObjectVariable

class CreateBlockVariable(var variableName: String = "", var blockId: String = "block") : FlowItem() {

    override val id = FlowItemIds.CREATE_BLOCK_VARIABLE

    override val nameTranslationKey = "action.createBlockVariable.name"
    override val detailTranslationKey = "action.createBlockVariable.detail"
    override val detailDefaultReplaces = listOf("block", "id")

    override val category = Category.BLOCK

    override fun isDataValid(): Boolean {
        return variableName != "" && blockId != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(variableName, blockId))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val name = source.replaceVariables(variableName)
        val id = source.replaceVariables(blockId)
        val item = try {
            Item.fromString(id)
        } catch (e: Exception) {
            throw InvalidFlowValueException(Language.get("action.createBlockVariable.block.notFound"))
        }

        val block = item.block
        if (item.id != 0 && block.id == 0) {
            throw InvalidFlowValueException(Language.get("action.createBlockVariable.block.notFound"))
        }

        val variable = BlockObjectVariable(block, name)
        source.addVariable(name, variable)
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            ExampleInput("@action.createBlockVariable.form.id", "1:0", blockId, true),
            ExampleInput("@action.form.resultVariableName", "block", variableName, true),
        )
    }

    override fun parseFromFormData(data: CustomFormResponseList): List<Any?> {
        return listOf(data[1], data[0])
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        variableName = contents.getString(0)
        blockId = contents.getString(1)
    }

    override fun serializeContents(): List<Any> {
        return listOf(variableName, blockId)
    }

    override fun getAddingVariables(): DummyVariableMap {
        return mapOf(
            variableName to DummyVariable(DummyVariable.Type.BLOCK, blockId)
        )
    }
}