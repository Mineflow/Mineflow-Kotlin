package tokyo.aieuo.mineflow.flowItem.action.entity

import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.EntityFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.EntityVariableDropdown
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleNumberInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable

@Suppress("LeakingThis")
open class SetHealth(entity: String = "", var health: String = ""): FlowItem(), EntityFlowItem {

    override val id = FlowItemIds.SET_HEALTH

    override val nameTranslationKey = "action.setHealth.name"
    override val detailTranslationKey = "action.setHealth.detail"
    override val detailDefaultReplaces = listOf("entity", "health")

    override val category = Category.ENTITY

    override var entityVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setEntityVariableName(entity)
    }

    override fun isDataValid(): Boolean {
        return getEntityVariableName() != "" && health != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getEntityVariableName(), health))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val health = source.replaceVariables(health)

        throwIfInvalidNumber(health, 1.0)

        val entity = getEntity(source)
        throwIfInvalidEntity(entity)

        entity.health = health.toFloat()
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            EntityVariableDropdown(variables, getEntityVariableName()),
            ExampleNumberInput("@action.setHealth.form.health", "20", health, true, 1.0),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setEntityVariableName(contents.getString(0))
        health = contents.getString(1)
    }

    override fun serializeContents(): List<Any> {
        return listOf(getEntityVariableName(), health)
    }

    override fun clone(): SetHealth {
        val item = super.clone() as SetHealth
        item.entityVariableNames = entityVariableNames.toMutableMap()
        return item
    }
}