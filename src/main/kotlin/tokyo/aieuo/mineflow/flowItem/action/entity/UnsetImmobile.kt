package tokyo.aieuo.mineflow.flowItem.action.entity

import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.EntityFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.EntityVariableDropdown
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language

class UnsetImmobile(entity: String = "") : FlowItem(), EntityFlowItem {

    override val id = FlowItemIds.UNSET_IMMOBILE

    override val nameTranslationKey = "action.unsetImmobile.name"
    override val detailTranslationKey = "action.unsetImmobile.detail"
    override val detailDefaultReplaces = listOf("entity")

    override val category = Category.ENTITY

    override var entityVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setEntityVariableName(entity)
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getEntityVariableName()))
    }

    override fun isDataValid(): Boolean {
        return getEntityVariableName() != ""
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val entity = getEntity(source)
        throwIfInvalidEntity(entity)

        entity.isImmobile = false
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            EntityVariableDropdown(variables, getEntityVariableName()),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setEntityVariableName(contents.getString(0))
    }

    override fun serializeContents(): List<Any> {
        return listOf(getEntityVariableName())
    }

    override fun clone(): UnsetImmobile {
        val item = super.clone() as UnsetImmobile
        item.entityVariableNames = entityVariableNames.toMutableMap()
        return item
    }
}
