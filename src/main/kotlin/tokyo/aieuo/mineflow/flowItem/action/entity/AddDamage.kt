package tokyo.aieuo.mineflow.flowItem.action.entity

import cn.nukkit.event.entity.EntityDamageEvent
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

class AddDamage(var entity: String = "", var damage: String = "", var cause: EntityDamageEvent.DamageCause = EntityDamageEvent.DamageCause.ENTITY_ATTACK)
    : FlowItem(), EntityFlowItem {

    override val id = FlowItemIds.ADD_DAMAGE

    override val nameTranslationKey = "action.addDamage.name"
    override val detailTranslationKey = "action.addDamage.detail"
    override val detailDefaultReplaces = listOf("entity", "damage")

    override val category = Category.ENTITY

    override var entityVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setEntityVariableName(entity)
    }

    override fun isDataValid(): Boolean {
        return damage != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getEntityVariableName(), damage))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val damage = source.replaceVariables(damage)

        throwIfInvalidNumber(damage, 1.0)

        val entity = getEntity(source)
        throwIfInvalidEntity(entity)

        val event = EntityDamageEvent(entity, cause, damage.toFloat())
        entity.attack(event)
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            EntityVariableDropdown(variables, getEntityVariableName()),
            ExampleNumberInput("@action.addDamage.form.damage", "10", damage, true, 1.0),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setEntityVariableName(contents.getString(0))
        damage = contents.getString(1)
        if (contents.size > 2) {
            cause = EntityDamageEvent.DamageCause.values()[contents.getInt(2)]
        }
    }

    override fun serializeContents(): List<Any> {
        return listOf(getEntityVariableName(), damage)
    }

    override fun clone(): AddDamage {
        val item = super.clone() as AddDamage
        item.entityVariableNames = entityVariableNames.toMutableMap()
        return item
    }
}