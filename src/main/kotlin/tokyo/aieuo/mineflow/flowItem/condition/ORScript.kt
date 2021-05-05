package tokyo.aieuo.mineflow.flowItem.condition

import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds

open class ORScript: AndScript() {

    override val id = FlowItemIds.CONDITION_OR

    override val nameTranslationKey = "condition.or.name"
    override val detailTranslationKey = "condition.or.detail"

    override fun getDetail(): String {
        val details = mutableListOf("-----------or-----------")
        for (condition in getConditions()) {
            details.add(condition.getDetail())
        }
        details.add("------------------------")
        return details.joinToString("\n")
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        for (condition in getConditions()) {
            for (result in condition.execute(source)) {
                if (result != FlowItemExecutor.Result.FAILURE) {
                    yield(FlowItemExecutor.Result.SUCCESS)
                    return@sequence
                }
            }
        }

        yield(FlowItemExecutor.Result.FAILURE)
    }
}