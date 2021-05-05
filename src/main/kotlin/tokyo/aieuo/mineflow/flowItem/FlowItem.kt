package tokyo.aieuo.mineflow.flowItem

import tokyo.aieuo.mineflow.exception.FlowItemLoadException
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Language

abstract class FlowItem: IFlowItem, Cloneable {

    override val detailDefaultReplaces: List<String> = listOf()

    override var customName: String = ""
    override val permission = PERMISSION_LEVEL_0

    companion object {
        const val PERMISSION_LEVEL_0 = 0
        const val PERMISSION_LEVEL_1 = 1
        const val PERMISSION_LEVEL_2 = 2

        @Suppress("UNCHECKED_CAST")
        fun loadEachSaveData(content: Map<String, Any>): FlowItem {
            val id = content["id"] as String
            val action = FlowItemFactory.get(id)
            if (action === null) {
                throw FlowItemLoadException(Language.get("action.not.found", listOf(id)))
            }

            action.customName = if (content.containsKey("customName")) content["customName"] as String else ""
            return action.loadSaveData(CustomFormResponseList(content["contents"] as List<Any>))
        }
    }

    abstract override fun loadSaveData(contents: CustomFormResponseList): FlowItem

    public override fun clone(): FlowItem {
        return super.clone() as FlowItem
    }
}
