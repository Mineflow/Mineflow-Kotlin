package tokyo.aieuo.mineflow.flowItem.action.item

import cn.nukkit.item.Item
import tokyo.aieuo.mineflow.exception.InvalidFlowValueException
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleNumberInput
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.obj.ItemObjectVariable
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList

class CreateItemVariable(var itemId: String = "", var itemCount: String = "", var itemName: String = "", var variableName: String = "item")
    : FlowItem() {

    override val id = FlowItemIds.CREATE_ITEM_VARIABLE

    override val nameTranslationKey = "action.createItemVariable.name"
    override val detailTranslationKey = "action.createItemVariable.detail"
    override val detailDefaultReplaces = listOf("item", "id", "count", "name")

    override val category = Category.ITEM

    override fun isDataValid(): Boolean {
        return variableName != "" && itemId != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(variableName, itemId, itemCount, itemName))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val name = source.replaceVariables(variableName)
        val id = source.replaceVariables(itemId)
        val count = source.replaceVariables(itemCount)
        val itemName = source.replaceVariables(itemName)
        val item = try {
            Item.fromString(id)
        } catch (e: Exception) {
            throw InvalidFlowValueException(Language.get("action.createItemVariable.item.notFound"))
        }
        if (item.id == 0) {
            throw InvalidFlowValueException(Language.get("action.createItemVariable.item.notFound"))
        }

        if (count.isNotEmpty()) {
            throwIfInvalidNumber(count, 0.0)
            item.count = count.toIntOrNull() ?: count.toDouble().toInt()
        } else {
            item.count = item.maxStackSize
        }
        if (itemName.isNotEmpty()) {
            item.customName = itemName
        }

        val variable = ItemObjectVariable(item)
        source.addVariable(name, variable)
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            ExampleInput("@action.createItemVariable.form.id", "1:0", itemId, true),
            ExampleNumberInput("@action.createItemVariable.form.count", "64", itemCount, false, 0.0),
            ExampleInput("@action.createItemVariable.form.name", "aieuo", itemName),
            ExampleInput("@action.form.resultVariableName", "item", variableName, true),
        )
    }

    override fun parseFromFormData(data: CustomFormResponseList): List<Any?> {
        return listOf(data[3], data[0], data[1], data[2])
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        variableName = contents.getString(0)
        itemId = contents.getString(1)
        itemCount = contents.getString(2)
        itemName = if (contents.size > 3) contents.getString(3) else ""
    }

    override fun serializeContents(): List<Any> {
        return listOf(variableName, itemId, itemCount, itemName)
    }

    override fun getAddingVariables(): Map<String, DummyVariable<DummyVariable.Type>> {
        return mapOf(
            variableName to DummyVariable(DummyVariable.Type.ITEM, itemId)
        )
    }
}