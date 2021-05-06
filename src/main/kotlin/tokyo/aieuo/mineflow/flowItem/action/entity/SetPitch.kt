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
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language

class SetPitch(var entity: String = "", var pitch: String = "") : FlowItem(), EntityFlowItem {

    override val id = FlowItemIds.SET_PITCH

    override val nameTranslationKey = "action.setPitch.name"
    override val detailTranslationKey = "action.setPitch.detail"
    override val detailDefaultReplaces = listOf("entity", "pitch")

    override val category = Category.ENTITY

    override var entityVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setEntityVariableName(entity)
    }

    override fun isDataValid(): Boolean {
        return getEntityVariableName() != "" && pitch != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getEntityVariableName(), pitch))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val pitch = source.replaceVariables(pitch)
        throwIfInvalidNumber(pitch)

        val entity = getEntity(source)
        throwIfInvalidEntity(entity)

        entity.setRotation(entity.yaw, pitch.toDouble())
        if (entity is Player) entity.teleport(Location.fromObject(entity, entity.level, entity.yaw, pitch.toDouble()))
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            EntityVariableDropdown(variables, getEntityVariableName()),
            ExampleNumberInput("@action.setPitch.form.pitch", "180", pitch, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setEntityVariableName(contents.getString(0))
        pitch = contents.getString(1)
    }

    override fun serializeContents(): List<Any> {
        return listOf(getEntityVariableName(), pitch)
    }

    override fun clone(): SetPitch {
        val item = super.clone() as SetPitch
        item.entityVariableNames = entityVariableNames.toMutableMap()
        return item
    }
}