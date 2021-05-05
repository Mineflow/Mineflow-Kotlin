package tokyo.aieuo.mineflow.flowItem.condition

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

class IsSneaking(entity: String = ""): FlowItem(), Condition, EntityFlowItem {

    override val id = FlowItemIds.IS_SNEAKING

    override val nameTranslationKey = "condition.isSneaking.name"
    override val detailTranslationKey = "condition.isSneaking.detail"
    override val detailDefaultReplaces = listOf("target")

    override val category = Category.ENTITY

    override var entityVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setEntityVariableName(entity)
    }

    override fun isDataValid(): Boolean {
        return getEntityVariableName() != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getEntityVariableName()))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val entity = getEntity(source)
        throwIfInvalidEntity(entity)

        yield(FlowItemExecutor.Result.CONTINUE)
        yield(if (entity.isSneaking) FlowItemExecutor.Result.SUCCESS else FlowItemExecutor.Result.FAILURE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            EntityVariableDropdown(variables, getEntityVariableName()),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        if (contents.isNotEmpty()) setEntityVariableName(contents.getString(0))
    }

    override fun serializeContents(): List<Any> {
        return listOf(getEntityVariableName())
    }

    override fun clone(): IsSneaking {
        val item = super.clone() as IsSneaking
        item.entityVariableNames = entityVariableNames.toMutableMap()
        return item
    }
}