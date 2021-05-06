package tokyo.aieuo.mineflow.flowItem.condition

import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds

class NorScript : ORScript() {

    override val id = FlowItemIds.CONDITION_NOR

    override val nameTranslationKey = "condition.nor.name"
    override val detailTranslationKey = "condition.nor.detail"

    override fun getDetail(): String {
        val details = mutableListOf("-----------nor-----------")
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
                    yield(FlowItemExecutor.Result.FAILURE)
                    return@sequence
                }
            }
        }

        yield(FlowItemExecutor.Result.SUCCESS)
    }
}