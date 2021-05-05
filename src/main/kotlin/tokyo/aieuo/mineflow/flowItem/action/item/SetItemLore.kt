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

class SetItemLore(item: String = "", lore: String = ""): FlowItem(), ItemFlowItem {

    override val id = FlowItemIds.SET_ITEM_LORE

    override val nameTranslationKey = "action.setLore.name"
    override val detailTranslationKey = "action.setLore.detail"
    override val detailDefaultReplaces = listOf("item", "lore")

    override val category = Category.ITEM

    override var itemVariableNames: MutableMap<String, String> = mutableMapOf()

    var lore = lore.split(";").map { it.trim() }.filter { it.isNotBlank() }

    init {
        setItemVariableName(item)
    }

    override fun isDataValid(): Boolean {
        return getItemVariableName() != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getItemVariableName(), lore.joinToString(";")))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val item = getItem(source)

        val lore = lore.map {
            source.replaceVariables(it)
        }

        item.setLore(*lore.toTypedArray())
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            ItemVariableDropdown(variables, getItemVariableName()),
            ExampleInput("@action.setLore.form.lore", "1;aiueo;abc", lore.joinToString(";"), false),
        )
    }

    override fun parseFromFormData(data: CustomFormResponseList): List<Any?> {
        return listOf(data[0], (data.getString(1)).split(";").map { it.trim() }.filter { it.isNotBlank() })
    }

    @Suppress("UNCHECKED_CAST")
    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setItemVariableName(contents.getString(0))
        lore = contents[1] as List<String>
    }

    override fun serializeContents(): List<Any> {
        return listOf(getItemVariableName(), lore)
    }

    override fun clone(): SetItemLore {
        val item = super.clone() as SetItemLore
        item.itemVariableNames = itemVariableNames.toMutableMap()
        return item
    }
}
