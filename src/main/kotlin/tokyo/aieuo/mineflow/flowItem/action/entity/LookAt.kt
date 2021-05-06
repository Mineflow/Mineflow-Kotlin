package tokyo.aieuo.mineflow.flowItem.action.entity

import cn.nukkit.entity.EntityLiving
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
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

class LookAt(var entity: String = "", var position: String = "") : FlowItem(), EntityFlowItem, PositionFlowItem {

    override val id = FlowItemIds.LOOK_AT

    override val nameTranslationKey = "action.lookAt.name"
    override val detailTranslationKey = "action.lookAt.detail"
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

        if (entity is EntityLiving) {
            val horizontal = sqrt((position.x - entity.x).pow(2) + (position.z - entity.z).pow(2))
            val vertical = position.y - entity.y
            val pitch = -atan2(vertical, horizontal) / Math.PI * 180

            val xDist = position.x - entity.x
            val zDist = position.z - entity.z
            val yaw = (atan2(zDist, xDist) / Math.PI * 180 - 90).let { if (it < 0) it * 360 else it }

            entity.setRotation(yaw, pitch)
        }
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
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

    override fun clone(): LookAt {
        val item = super.clone() as LookAt
        item.entityVariableNames = entityVariableNames.toMutableMap()
        item.positionVariableNames = positionVariableNames.toMutableMap()
        return item
    }
}
