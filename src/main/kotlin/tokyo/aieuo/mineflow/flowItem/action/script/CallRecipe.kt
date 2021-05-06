package tokyo.aieuo.mineflow.flowItem.action.script

import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds

class CallRecipe(recipeName: String = "", args: List<String> = listOf()) : ExecuteRecipe(recipeName, args) {

    override val id = FlowItemIds.CALL_RECIPE

    override val nameTranslationKey = "action.callRecipe.name"
    override val detailTranslationKey = "action.callRecipe.detail"

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val recipe = getRecipe(source).clone()
        val args = getArguments(source)

        recipe.executeAllTargets(source.target, mapOf(), source.event, args, source)
        yield(FlowItemExecutor.Result.AWAIT)
    }
}