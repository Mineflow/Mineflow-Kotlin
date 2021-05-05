package tokyo.aieuo.mineflow.flowItem.action.event

import tokyo.aieuo.mineflow.exception.InvalidFlowValueException
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.Language
import cn.nukkit.event.Cancellable
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList

class EventCancel: FlowItem() {

    override val id = FlowItemIds.EVENT_CANCEL

    override val nameTranslationKey = "action.eventCancel.name"
    override val detailTranslationKey = "action.eventCancel.detail"

    override val category = Category.EVENT

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val event = source.event
        if (event !is Cancellable) {
            throw InvalidFlowValueException(Language.get("action.eventCancel.notCancelable"))
        }
        event.setCancelled()
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