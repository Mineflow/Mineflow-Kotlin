package tokyo.aieuo.mineflow.flowItem.condition

import cn.nukkit.entity.EntityCreature
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds

class IsCreatureVariable(entity: String = "") : IsActiveEntityVariable(entity) {

    override val id = FlowItemIds.IS_CREATURE_VARIABLE

    override val nameTranslationKey = "condition.isCreatureVariable.name"
    override val detailTranslationKey = "condition.isCreatureVariable.detail"

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val entity = getEntity(source)
        throwIfInvalidEntity(entity)

        val result = entity is EntityCreature
        yield(if (result) FlowItemExecutor.Result.SUCCESS else FlowItemExecutor.Result.FAILURE)
    }
}