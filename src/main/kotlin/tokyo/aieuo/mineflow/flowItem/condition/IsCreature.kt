package tokyo.aieuo.mineflow.flowItem.condition

import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.utils.EntityHolder
import cn.nukkit.entity.EntityCreature
import tokyo.aieuo.mineflow.flowItem.FlowItemIds

class IsCreature(entityId: String = ""): IsActiveEntity(entityId) {

    override val id = FlowItemIds.IS_CREATURE

    override val nameTranslationKey = "condition.isCreature.name"
    override val detailTranslationKey = "condition.isCreature.detail"

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val id = source.replaceVariables(entityId)
        throwIfInvalidNumber(id)

        val entity = EntityHolder.findEntity(id.toLong())

        yield(if (entity is EntityCreature) FlowItemExecutor.Result.SUCCESS else FlowItemExecutor.Result.FAILURE)
    }
}