package tokyo.aieuo.mineflow.flowItem.condition

import tokyo.aieuo.mineflow.flowItem.*
import tokyo.aieuo.mineflow.formAPI.element.Button
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.ui.FlowItemContainerForm
import tokyo.aieuo.mineflow.utils.Category

open class AndScript: FlowItem(), Condition, FlowItemContainer {

    override val id = FlowItemIds.CONDITION_AND

    override val nameTranslationKey = "condition.and.name"
    override val detailTranslationKey = "condition.and.detail"

    override val category = Category.SCRIPT

    override var items: MutableMap<String, MutableList<FlowItem>> = mutableMapOf()

    override fun getDetail(): String {
        val details = mutableListOf("----------and-----------")
        for (condition in getConditions()) {
            details.add(condition.getDetail())
        }
        details.add("------------------------")
        return details.joinToString("\n")
    }

    override fun getContainerName(): String {
        return if (customName.isEmpty()) getName() else customName
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        for (condition in getConditions()) {
            for (result in condition.execute(source)) {
                if (result == FlowItemExecutor.Result.FAILURE) {
                    yield(FlowItemExecutor.Result.FAILURE)
                    return@sequence
                }
            }
        }

        yield(FlowItemExecutor.Result.SUCCESS)
    }

    override fun hasCustomMenu(): Boolean {
        return true
    }

    override fun getCustomMenuButtons(): List<Button> {
        return listOf(
            Button("@condition.edit") { player ->
                FlowItemContainerForm.sendActionList(player, this, FlowItemContainer.CONDITION)
            },
        )
    }

    @Suppress("UNCHECKED_CAST")
    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        for (content in contents as List<Map<String, Any>>) {
            val condition = loadEachSaveData(content)
            addItem(condition, FlowItemContainer.CONDITION)
        }
    }

    override fun serializeContents(): List<Any> {
        return getConditions()
    }

    override fun isDataValid(): Boolean {
        return true
    }

    override fun clone(): AndScript {
        val item = super.clone() as AndScript

        item.items = mutableMapOf()
        val conditions = mutableListOf<FlowItem>()
        for (condition in getConditions()) {
            conditions.add(condition.clone())
        }
        item.setItems(conditions, FlowItemContainer.CONDITION)

        return item
    }
}