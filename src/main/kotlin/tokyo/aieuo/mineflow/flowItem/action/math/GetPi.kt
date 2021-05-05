package tokyo.aieuo.mineflow.flowItem.action.math

import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.variable.NumberVariable

class GetPi(resultName: String = "pi"): TypeGetMathVariable(resultName) {

    override val id = FlowItemIds.GET_PI

    override val nameTranslationKey = "action.getPi.name"
    override val detailTranslationKey = "action.getPi.detail"

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val resultName = source.replaceVariables(resultName)
        source.addVariable(resultName, NumberVariable(Math.PI))
        yield(FlowItemExecutor.Result.CONTINUE)
    }
}