package tokyo.aieuo.mineflow.flowItem.condition

import cn.nukkit.Player
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.utils.Category

class IsPlayerVariable(entity: String = "") : IsActiveEntityVariable(entity) {

    override val id = FlowItemIds.IS_PLAYER_VARIABLE

    override val nameTranslationKey = "condition.isPlayerVariable.name"
    override val detailTranslationKey = "condition.isPlayerVariable.detail"

    override val category = Category.PLAYER

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val entity = getEntity(source)
        throwIfInvalidEntity(entity)

        val result = entity is Player
        yield(if (result) FlowItemExecutor.Result.SUCCESS else FlowItemExecutor.Result.FAILURE)
    }
}