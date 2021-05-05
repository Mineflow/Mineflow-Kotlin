package tokyo.aieuo.mineflow.flowItem.action.entity

import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds

class SetMaxHealth(entity: String = "", health: String = ""): SetHealth(entity, health) {

    override val id = FlowItemIds.SET_MAX_HEALTH

    override val nameTranslationKey = "action.setMaxHealth.name"
    override val detailTranslationKey = "action.setMaxHealth.detail"

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val health = source.replaceVariables(health)
        throwIfInvalidNumber(health, 1.0)

        val entity = getEntity(source)
        throwIfInvalidEntity(entity)

        entity.maxHealth = health.toIntOrNull() ?: health.toDouble().toInt()
        yield(FlowItemExecutor.Result.CONTINUE)
    }
}