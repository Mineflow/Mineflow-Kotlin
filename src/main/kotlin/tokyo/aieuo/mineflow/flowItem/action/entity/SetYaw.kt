package tokyo.aieuo.mineflow.flowItem.action.entity

import cn.nukkit.Player
import cn.nukkit.level.Location
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

class SetYaw(entity: String = "", var yaw: String = ""): FlowItem(), EntityFlowItem {

    override val id = FlowItemIds.SET_YAW

    override val nameTranslationKey = "action.setYaw.name"
    override val detailTranslationKey = "action.setYaw.detail"
    override val detailDefaultReplaces = listOf("entity", "yaw")

    override val category = Category.ENTITY

    override var entityVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setEntityVariableName(entity)
    }

    override fun isDataValid(): Boolean {
        return getEntityVariableName() != "" && yaw != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getEntityVariableName(), yaw))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val yaw = source.replaceVariables(yaw)
        throwIfInvalidNumber(yaw)

        val entity = getEntity(source)
        throwIfInvalidEntity(entity)

        entity.setRotation(yaw.toDouble(), entity.pitch)
        if (entity is Player) entity.teleport(Location.fromObject(entity, entity.level, yaw.toDouble(), entity.pitch))
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            EntityVariableDropdown(variables, getEntityVariableName()),
            ExampleNumberInput("@action.setYaw.form.yaw", "180", yaw, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setEntityVariableName(contents.getString(0))
        yaw = contents.getString(1)
    }

    override fun serializeContents(): List<Any> {
        return listOf(getEntityVariableName(), yaw)
    }

    override fun clone(): SetYaw {
        val item = super.clone() as SetYaw
        item.entityVariableNames = entityVariableNames.toMutableMap()
        return item
    }
}