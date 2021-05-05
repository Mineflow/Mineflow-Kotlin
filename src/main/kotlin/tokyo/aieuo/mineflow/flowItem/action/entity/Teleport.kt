package tokyo.aieuo.mineflow.flowItem.action.entity

import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.EntityFlowItem
import tokyo.aieuo.mineflow.flowItem.base.PositionFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.EntityVariableDropdown
import tokyo.aieuo.mineflow.formAPI.element.mineflow.PositionVariableDropdown
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable

class Teleport(entity: String = "", position: String = ""): FlowItem(), EntityFlowItem, PositionFlowItem {

    override val id = FlowItemIds.TELEPORT

    override val nameTranslationKey = "action.teleport.name"
    override val detailTranslationKey = "action.teleport.detail"
    override val detailDefaultReplaces = listOf("entity", "position")

    override val category = Category.ENTITY

    override var entityVariableNames: MutableMap<String, String> = mutableMapOf()
    override var positionVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setEntityVariableName(entity)
        setPositionVariableName(position)
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getEntityVariableName(), getPositionVariableName()))
    }

    override fun isDataValid(): Boolean {
        return getEntityVariableName() != "" && getPositionVariableName() != ""
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val entity = getEntity(source)
        throwIfInvalidEntity(entity)

        val position = getPosition(source)

        entity.teleport(position)
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            EntityVariableDropdown(variables, getEntityVariableName()),
            PositionVariableDropdown(variables, getPositionVariableName()),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setEntityVariableName(contents.getString(0))
        setPositionVariableName(contents.getString(1))
    }

    override fun serializeContents(): List<Any> {
        return listOf(getEntityVariableName(), getPositionVariableName())
    }

    override fun clone(): Teleport {
        val item = super.clone() as Teleport
        item.entityVariableNames = entityVariableNames.toMutableMap()
        item.positionVariableNames = positionVariableNames.toMutableMap()
        return item
    }
}
