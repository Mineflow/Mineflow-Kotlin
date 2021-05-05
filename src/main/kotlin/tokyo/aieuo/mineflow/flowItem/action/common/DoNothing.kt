package tokyo.aieuo.mineflow.flowItem.action.common

import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category

class DoNothing: FlowItem() {

    override val id = FlowItemIds.DO_NOTHING

    override val nameTranslationKey = "action.doNothing.name"
    override val detailTranslationKey = "action.doNothing.detail"

    override val category = Category.COMMON

    override fun execute(source: FlowItemExecutor) = sequence {
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