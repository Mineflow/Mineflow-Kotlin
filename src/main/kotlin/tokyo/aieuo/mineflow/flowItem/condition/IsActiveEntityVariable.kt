package tokyo.aieuo.mineflow.flowItem.condition

import cn.nukkit.Player
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

@Suppress("LeakingThis")
open class IsActiveEntityVariable(entity: String = ""): FlowItem(), Condition, EntityFlowItem {

    override val id = FlowItemIds.IS_ACTIVE_ENTITY_VARIABLE

    override val nameTranslationKey = "condition.isActiveEntityVariable.name"
    override val detailTranslationKey = "condition.isActiveEntityVariable.detail"
    override val detailDefaultReplaces = listOf("entity")

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

        val result = entity.isAlive && !entity.isClosed && !(entity is Player && !entity.isOnline)
        yield(if (result) FlowItemExecutor.Result.SUCCESS else FlowItemExecutor.Result.FAILURE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            EntityVariableDropdown(variables),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setEntityVariableName(contents.getString(0))
    }

    override fun serializeContents(): List<Any> {
        return listOf(getEntityVariableName())
    }

    override fun clone(): IsActiveEntityVariable {
        val item = super.clone() as IsActiveEntityVariable
        item.entityVariableNames = entityVariableNames.toMutableMap()
        return item
    }
}