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
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language

class SetItemDamage(item: String = "", var damage: String = "") : FlowItem(), ItemFlowItem {

    override val id = FlowItemIds.SET_ITEM_DAMAGE

    override val nameTranslationKey = "action.setItemDamage.name"
    override val detailTranslationKey = "action.setItemDamage.detail"
    override val detailDefaultReplaces = listOf("item", "damage")

    override val category = Category.ITEM

    override var itemVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setItemVariableName(item)
    }

    override fun isDataValid(): Boolean {
        return getItemVariableName() != "" && damage != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getItemVariableName(), damage))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        damage = source.replaceVariables(damage)
        throwIfInvalidNumber(damage, 0.0)

        val item = getItem(source)

        item.damage = damage.toInt()
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            ItemVariableDropdown(variables, getItemVariableName()),
            ExampleNumberInput("@action.setDamage.form.damage", "0", damage, true, 0.0),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setItemVariableName(contents.getString(0))
        damage = contents.getString(1)
    }

    override fun serializeContents(): List<Any> {
        return listOf(getItemVariableName(), damage)
    }

    override fun clone(): SetItemDamage {
        val item = super.clone() as SetItemDamage
        item.itemVariableNames = itemVariableNames.toMutableMap()
        return item
    }
}
