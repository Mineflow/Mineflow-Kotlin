package tokyo.aieuo.mineflow.flowItem.action.entity

import cn.nukkit.entity.EntityLiving
import cn.nukkit.potion.Effect
import tokyo.aieuo.mineflow.exception.InvalidFlowValueException
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.EntityFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.Toggle
import tokyo.aieuo.mineflow.formAPI.element.mineflow.EntityVariableDropdown
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleNumberInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language

class AddEffect(var entity: String = "", var effectId: String = "", var time: String = "300", var power: String = "1") :
    FlowItem(), EntityFlowItem {

    override val id = FlowItemIds.ADD_EFFECT

    override val nameTranslationKey = "action.addEffect.name"
    override val detailTranslationKey = "action.addEffect.detail"
    override val detailDefaultReplaces = listOf("entity", "id", "power", "time")

    override val category = Category.ENTITY

    override var entityVariableNames: MutableMap<String, String> = mutableMapOf()

    var visible = false

    init {
        setEntityVariableName(entity)
    }

    override fun isDataValid(): Boolean {
        return getEntityVariableName() != "" && effectId != "" && power != "" && time != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getEntityVariableName(), effectId, power, time))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val effectId = source.replaceVariables(effectId)
        val time = source.replaceVariables(time)
        val power = source.replaceVariables(power)

        throwIfInvalidNumber(time)
        throwIfInvalidNumber(power)

        val entity = getEntity(source)
        throwIfInvalidEntity(entity)

        val effect = try {
            Effect.getEffect(effectId.toInt())
        } catch (e: Exception) {
            try {
                Effect.getEffectByName(effectId)
            } catch (e: Exception) {
                throw InvalidFlowValueException(Language.get("action.effect.notFound", listOf(effectId)))
            }
        }

        effect.duration = (time.toIntOrNull() ?: time.toDouble().toInt()) * 20
        effect.amplifier = (power.toIntOrNull() ?: power.toDouble().toInt()) - 1
        effect.isVisible = visible

        if (entity is EntityLiving) {
            entity.addEffect(effect)
        }
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            EntityVariableDropdown(variables, getEntityVariableName()),
            ExampleInput("@action.addEffect.form.effect", "1", effectId, true),
            ExampleNumberInput("@action.addEffect.form.time", "300", time, true, 1.0),
            ExampleNumberInput("@action.addEffect.form.power", "1", power, true),
            Toggle("@action.addEffect.form.visible", visible),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setEntityVariableName(contents.getString(0))
        effectId = contents.getString(1)
        time = contents.getString(2)
        power = contents.getString(3)
        visible = contents.getBoolean(4)
    }

    override fun serializeContents(): List<Any> {
        return listOf(getEntityVariableName(), effectId, time, power, visible)
    }

    override fun clone(): AddEffect {
        val item = super.clone() as AddEffect
        item.entityVariableNames = entityVariableNames.toMutableMap()
        return item
    }
}