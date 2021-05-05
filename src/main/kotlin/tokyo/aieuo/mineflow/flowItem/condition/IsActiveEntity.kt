package tokyo.aieuo.mineflow.flowItem.condition

import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.EntityHolder
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable

open class IsActiveEntity(var entityId: String = ""): FlowItem(), Condition {

    override val id = FlowItemIds.IS_ACTIVE_ENTITY

    override val nameTranslationKey = "condition.isActiveEntity.name"
    override val detailTranslationKey = "condition.isActiveEntity.detail"
    override val detailDefaultReplaces = listOf("id")

    override val category = Category.ENTITY

    override fun isDataValid(): Boolean {
        return entityId != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(entityId))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val id = source.replaceVariables(entityId)
        throwIfInvalidNumber(id)

        val active = EntityHolder.isActive(id.toLong())
        yield(if (active) FlowItemExecutor.Result.SUCCESS else FlowItemExecutor.Result.FAILURE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            ExampleInput("@condition.isActiveEntity.form.entityId", "aieuo", entityId, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        entityId = contents.getString(0)
    }

    override fun serializeContents(): List<Any> {
        return listOf(entityId)
    }
}