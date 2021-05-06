package tokyo.aieuo.mineflow.flowItem.condition

import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.EntityHolder

class IsPlayer(entityId: String = "") : IsActiveEntity(entityId) {

    override val id = FlowItemIds.IS_PLAYER

    override val nameTranslationKey = "condition.isPlayer.name"
    override val detailTranslationKey = "condition.isPlayer.detail"

    override val category = Category.PLAYER

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val id = source.replaceVariables(entityId)
        throwIfInvalidNumber(id)

        val result = EntityHolder.isPlayer(id.toLong())
        yield(if (result) FlowItemExecutor.Result.SUCCESS else FlowItemExecutor.Result.FAILURE)
    }
}