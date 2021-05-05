package tokyo.aieuo.mineflow.flowItem.action.inventory

import cn.nukkit.entity.EntityHuman
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.EntityFlowItem
import tokyo.aieuo.mineflow.flowItem.base.ItemFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Dropdown
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.EntityVariableDropdown
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ItemVariableDropdown
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable

class EquipArmor(entity: String = "", item: String = "", var index: String = ""): FlowItem(), EntityFlowItem, ItemFlowItem {

    override val id = FlowItemIds.EQUIP_ARMOR

    override val nameTranslationKey = "action.equipArmor.name"
    override val detailTranslationKey = "action.equipArmor.detail"
    override val detailDefaultReplaces = listOf("entity", "item", "index")

    override val category = Category.INVENTORY

    override var entityVariableNames: MutableMap<String, String> = mutableMapOf()
    override var itemVariableNames: MutableMap<String, String> = mutableMapOf()

    private val places = listOf(
        "action.equipArmor.helmet",
        "action.equipArmor.chestplate",
        "action.equipArmor.leggings",
        "action.equipArmor.boots",
    )

    init {
        setEntityVariableName(entity)
        setItemVariableName(item)
    }

    override fun isDataValid(): Boolean {
        return getEntityVariableName() != "" && getItemVariableName() != "" && index != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getEntityVariableName(), getItemVariableName(), Language.get(places[index.toInt()])))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val index = source.replaceVariables(index)

        throwIfInvalidNumber(index, 0.0, 3.0)

        val entity = getEntity(source)
        throwIfInvalidEntity(entity)

        val item = getItem(source)

        if (entity is EntityHuman) {
            entity.inventory.setArmorItem(index.toInt(), item)
        }
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            EntityVariableDropdown(variables, getEntityVariableName()),
            ItemVariableDropdown(variables, getItemVariableName()),
            Dropdown("@action.equipArmor.form.index", places.map { Language.get(it) }, index.toInt()),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setEntityVariableName(contents.getString(0))
        setItemVariableName(contents.getString(1))
        index = contents.getString(2)
    }

    override fun serializeContents(): List<Any> {
        return listOf(getEntityVariableName(), getItemVariableName(), index)
    }

    override fun clone(): EquipArmor {
        val item = super.clone() as EquipArmor
        item.entityVariableNames = entityVariableNames.toMutableMap()
        item.itemVariableNames = itemVariableNames.toMutableMap()
        return item
    }
}