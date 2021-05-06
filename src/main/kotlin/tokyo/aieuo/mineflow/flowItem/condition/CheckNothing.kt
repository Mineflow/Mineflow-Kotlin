package tokyo.aieuo.mineflow.flowItem.condition

import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category

class CheckNothing : FlowItem(), Condition {

    override val id = FlowItemIds.CHECK_NOTHING

    override val nameTranslationKey = "condition.noCheck.name"
    override val detailTranslationKey = "condition.noCheck.detail"

    override val category = Category.COMMON

    override fun execute(source: FlowItemExecutor) = sequence {
        yield(FlowItemExecutor.Result.SUCCESS)
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