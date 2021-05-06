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
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language

class SetScale(entity: String = "", var scale: String = "") : FlowItem(), EntityFlowItem {

    override val id = FlowItemIds.SET_SCALE

    override val nameTranslationKey = "action.setScale.name"
    override val detailTranslationKey = "action.setScale.detail"
    override val detailDefaultReplaces = listOf("entity", "scale")

    override val category = Category.ENTITY

    override var entityVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setEntityVariableName(entity)
    }

    override fun isDataValid(): Boolean {
        return getEntityVariableName() != "" && scale != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getEntityVariableName(), scale))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val health = source.replaceVariables(scale)

        throwIfInvalidNumber(health, 0.0)

        val entity = getEntity(source)
        throwIfInvalidEntity(entity)

        entity.scale = health.toFloat()
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            EntityVariableDropdown(variables, getEntityVariableName()),
            ExampleNumberInput("@action.setScale.form.scale", "1", scale, true, 0.0, excludes = listOf(0.0)),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setEntityVariableName(contents.getString(0))
        scale = contents.getString(1)
    }

    override fun serializeContents(): List<Any> {
        return listOf(getEntityVariableName(), scale)
    }

    override fun clone(): SetScale {
        val item = super.clone() as SetScale
        item.entityVariableNames = entityVariableNames.toMutableMap()
        return item
    }
}