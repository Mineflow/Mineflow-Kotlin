package tokyo.aieuo.mineflow.flowItem.action.entity

import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.EntityFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.EntityVariableDropdown
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable

class SetImmobile(var entity: String = ""): FlowItem(), EntityFlowItem {

    override val id = FlowItemIds.SET_IMMOBILE

    override val nameTranslationKey = "action.setImmobile.name"
    override val detailTranslationKey = "action.setImmobile.detail"
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

        entity.isImmobile = true
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
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

    override fun clone(): SetImmobile {
        val item = super.clone() as SetImmobile
        item.entityVariableNames = entityVariableNames.toMutableMap()
        return item
    }
}
