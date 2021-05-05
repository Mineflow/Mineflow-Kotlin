package tokyo.aieuo.mineflow.flowItem.action.script

import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category

class ExitRecipe: FlowItem() {

    override val id = FlowItemIds.EXIT_RECIPE

    override val nameTranslationKey = "action.exit.name"
    override val detailTranslationKey = "action.exit.detail"

    override val category = Category.SCRIPT

    override fun execute(source: FlowItemExecutor) = sequence {
        source.exit()
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun isDataValid(): Boolean {
        return true
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
    }

    override fun serializeContents(): List<Any> {
        return listOf()
    }
}