package tokyo.aieuo.mineflow.flowItem.action.script

import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemContainer
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.formAPI.element.Button
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.ui.FlowItemContainerForm
import tokyo.aieuo.mineflow.utils.Category

class ElseAction(actions: List<FlowItem> = listOf()): FlowItem(), FlowItemContainer {

    override val id = FlowItemIds.ACTION_ELSE

    override val nameTranslationKey = "action.else.name"
    override val detailTranslationKey = "action.else.description"

    override val category = Category.SCRIPT

    override var items: MutableMap<String, MutableList<FlowItem>> = mutableMapOf()

    init {
        setItems(actions.toMutableList(), FlowItemContainer.ACTION)
    }

    override fun getDetail(): String {
        val details = mutableListOf("§7=============§f else §7=============§f")
        for (action in getActions()) {
            details.add(action.getDetail())
        }
        details.add("§7================================§f")
        return details.joinToString("\n")
    }

    override fun getContainerName(): String {
        return if (customName.isEmpty()) getName() else customName
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        val lastResult = source.lastResult

        if (lastResult == FlowItemExecutor.Result.FAILURE) {
            yieldAll(FlowItemExecutor(getActions(), source.target, parent = source).executeGenerator())
        }
        yield(FlowItemExecutor.Result.SUCCESS)
    }

    override fun hasCustomMenu(): Boolean {
        return true
    }

    override fun getCustomMenuButtons(): List<Button> {
        return listOf(
            Button("@action.edit") { player ->
                FlowItemContainerForm.sendActionList(player, this, FlowItemContainer.ACTION)
            },
        )
    }

    @Suppress("UNCHECKED_CAST")
    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        for (content in contents as List<Map<String, Any>>) {
            val action = loadEachSaveData(content)
            addItem(action, FlowItemContainer.ACTION)
        }
    }

    override fun serializeContents(): List<Any> {
        return getActions()
    }

    override fun isDataValid(): Boolean {
        return true
    }

    override fun clone(): ElseAction {
        val item = super.clone() as ElseAction

        item.items = mutableMapOf()
        val actions = mutableListOf<FlowItem>()
        for (action in getActions()) {
            actions.add(action.clone())
        }
        item.setItems(actions, FlowItemContainer.ACTION)

        return item
    }
}