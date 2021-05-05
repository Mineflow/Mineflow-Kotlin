package tokyo.aieuo.mineflow.flowItem.action.script

import tokyo.aieuo.mineflow.flowItem.*
import tokyo.aieuo.mineflow.formAPI.element.Button
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.ui.FlowItemContainerForm
import tokyo.aieuo.mineflow.utils.Category

@Suppress("LeakingThis")
open class IFAction(conditions: List<FlowItem> = listOf(), actions: List<FlowItem> = listOf()): FlowItem(), FlowItemContainer {

    override val id = FlowItemIds.ACTION_IF

    override val nameTranslationKey = "action.if.name"
    override val detailTranslationKey = "action.if.description"

    override val category = Category.SCRIPT

    override var items: MutableMap<String, MutableList<FlowItem>> = mutableMapOf()

    init {
        setItems(conditions.toMutableList(), FlowItemContainer.CONDITION)
        setItems(actions.toMutableList(), FlowItemContainer.ACTION)
    }

    override fun getDetail(): String {
        val details = mutableListOf("", "§7=============§f if §7===============§f")
        for (condition in getConditions()) {
            details.add(condition.getDetail())
        }
        details.add("§7~~~~~~~~~~~~~~~~~~~~~~~~~~~§f")
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
        for (condition in getConditions()) {
            for (result in condition.execute(source)) {
                if (result == FlowItemExecutor.Result.FAILURE) {
                    yield(FlowItemExecutor.Result.FAILURE)
                    return@sequence
                }
            }
        }

        yieldAll(FlowItemExecutor(getActions(), source.target, parent = source).executeGenerator())
        yield(FlowItemExecutor.Result.SUCCESS)
    }

    override fun isDataValid(): Boolean {
        return true
    }

    override fun hasCustomMenu(): Boolean {
        return true
    }

    override fun getCustomMenuButtons(): List<Button> {
        return listOf(
            Button("@condition.edit") { player ->
                FlowItemContainerForm.sendActionList(player, this, FlowItemContainer.CONDITION)
            },
            Button("@action.edit") { player ->
                FlowItemContainerForm.sendActionList(player, this, FlowItemContainer.ACTION)
            },
        )
    }

    @Suppress("UNCHECKED_CAST")
    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        for (content in contents[0] as List<Map<String, Any>>) {
            val condition = loadEachSaveData(content)
            addItem(condition, FlowItemContainer.CONDITION)
        }

        for (content in contents[1] as List<Map<String, Any>>) {
            val action = loadEachSaveData(content)
            addItem(action, FlowItemContainer.ACTION)
        }
    }

    override fun serializeContents(): List<Any> {
        return listOf(
            getConditions(),
            getActions(),
        )
    }

    override fun clone(): IFAction {
        val item = super.clone() as IFAction

        item.items = mutableMapOf()
        val conditions = mutableListOf<FlowItem>()
        for (condition in getConditions()) {
            conditions.add(condition.clone())
        }
        item.setItems(conditions, FlowItemContainer.CONDITION)

        val actions = mutableListOf<FlowItem>()
        for (action in getActions()) {
            actions.add(action.clone())
        }
        item.setItems(actions, FlowItemContainer.ACTION)

        return item
    }
}