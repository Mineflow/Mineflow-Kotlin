package tokyo.aieuo.mineflow.flowItem.condition

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
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language
import kotlin.math.max
import kotlin.math.min

class InArea(entity: String = "", pos1: String = "", pos2: String = "") :
    FlowItem(), Condition, EntityFlowItem, PositionFlowItem {

    override val id = FlowItemIds.IN_AREA

    override val nameTranslationKey = "condition.inArea.name"
    override val detailTranslationKey = "condition.inArea.detail"
    override val detailDefaultReplaces = listOf("target", "pos1", "pos2")

    override val category = Category.ENTITY

    override var entityVariableNames: MutableMap<String, String> = mutableMapOf()
    override var positionVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setEntityVariableName(entity)
        setPositionVariableName(pos1, "pos1")
        setPositionVariableName(pos2, "pos2")
    }

    override fun isDataValid(): Boolean {
        return getEntityVariableName() != "" && getPositionVariableName("pos1") != "" && getPositionVariableName("pos2") != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(
            detailTranslationKey, listOf(
                getEntityVariableName(),
                getPositionVariableName("pos1"),
                getPositionVariableName("pos2")
            )
        )
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val entity = getEntity(source)
        throwIfInvalidEntity(entity)

        val pos1 = getPosition(source, "pos1")
        val pos2 = getPosition(source, "pos2")
        val pos = entity.floor()

        val result = pos.x >= min(pos1.x, pos2.x) && pos.x <= max(pos1.x, pos2.x)
                && pos.y >= min(pos1.y, pos2.y) && pos.y <= max(pos1.y, pos2.y)
                && pos.z >= min(pos1.z, pos2.z) && pos.z <= max(pos1.z, pos2.z)
        yield(if (result) FlowItemExecutor.Result.SUCCESS else FlowItemExecutor.Result.FAILURE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            EntityVariableDropdown(variables, getEntityVariableName()),
            PositionVariableDropdown(variables, getPositionVariableName("pos1"), "@condition.inArea.form.pos1"),
            PositionVariableDropdown(variables, getPositionVariableName("pos2"), "@condition.inArea.form.pos2"),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setEntityVariableName(contents.getString(0))
        setPositionVariableName(contents.getString(1), "pos1")
        setPositionVariableName(contents.getString(2), "pos2")
    }

    override fun serializeContents(): List<Any> {
        return listOf(
            getEntityVariableName(),
            getPositionVariableName("pos1"),
            getPositionVariableName("pos2")
        )
    }

    override fun clone(): InArea {
        val item = super.clone() as InArea
        item.entityVariableNames = entityVariableNames.toMutableMap()
        item.positionVariableNames = positionVariableNames.toMutableMap()
        return item
    }
}