package tokyo.aieuo.mineflow.flowItem.action.script

import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds

class ElseifAction(conditions: List<FlowItem> = listOf(), actions: List<FlowItem> = listOf()): IFAction(conditions, actions) {

    override val id = FlowItemIds.ACTION_ELSEIF

    override val nameTranslationKey = "action.elseif.name"
    override val detailTranslationKey = "action.elseif.description"

    override fun getDetail(): String {
        val details = mutableListOf("§7=============§f elseif §7=============§f")
        for (condition in getConditions()) {
            details.add(condition.getDetail())
        }
        details.add("§7~~~~~~~~~~~~~~~~~~~~~~~~~~~§f")
        for (action in getActions()) {
            details.add(action.getDetail())
        }
        details.add("§7================================§f")
        return details.joinToString("\n")
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        val lastResult = source.lastResult
        if (lastResult == FlowItemExecutor.Result.FAILURE) {
            for (condition in getConditions()) {
                for (result in condition.execute(source)) {
                    if (result == FlowItemExecutor.Result.FAILURE) {
                        yield(FlowItemExecutor.Result.FAILURE)
                        return@sequence
                    }
                }
            }

            yieldAll(FlowItemExecutor(getActions(), source.target, parent = source).executeGenerator())
        }

        yield(FlowItemExecutor.Result.SUCCESS)
    }
}