package tokyo.aieuo.mineflow.flowItem.action.math

import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.variable.NumberVariable

class GetE(resultName: String = "e") : TypeGetMathVariable(resultName) {

    override val id = FlowItemIds.GET_E

    override val nameTranslationKey = "action.getE.name"
    override val detailTranslationKey = "action.getE.detail"

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val resultName = source.replaceVariables(resultName)
        source.addVariable(resultName, NumberVariable(Math.E))
        yield(FlowItemExecutor.Result.CONTINUE)
    }
}