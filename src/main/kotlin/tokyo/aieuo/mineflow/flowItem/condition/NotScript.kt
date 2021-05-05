package tokyo.aieuo.mineflow.flowItem.condition

import tokyo.aieuo.mineflow.flowItem.FlowItemIds

class NotScript: NandScript() {

    override val id = FlowItemIds.CONDITION_NOT

    override val nameTranslationKey = "condition.not.name"
    override val detailTranslationKey = "condition.not.detail"

    override fun getDetail(): String {
        val details = mutableListOf("-----------not-----------")
        for (condition in getConditions()) {
            details.add(condition.getDetail())
        }
        details.add("------------------------")
        return details.joinToString("\n")
    }
}