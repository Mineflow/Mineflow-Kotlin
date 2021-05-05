package tokyo.aieuo.mineflow.flowItem.action.script

import cn.nukkit.Player
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemContainer
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.formAPI.CustomForm
import tokyo.aieuo.mineflow.formAPI.element.Button
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleNumberInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.ui.FlowItemContainerForm
import tokyo.aieuo.mineflow.ui.FlowItemForm
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.NumberVariable

class ForAction(actions: List<FlowItem> = listOf()): FlowItem(), FlowItemContainer {

    override val id = FlowItemIds.ACTION_FOR

    override val nameTranslationKey = "action.for.name"
    override val detailTranslationKey = "action.for.description"

    override val category = Category.SCRIPT

    override val permission = PERMISSION_LEVEL_1

    var counterName = "i"
    var startIndex = "0"
    var endIndex = "9"
    var fluctuation = "1"

    override var items: MutableMap<String, MutableList<FlowItem>> = mutableMapOf()

    init {
        setItems(actions.toMutableList(), FlowItemContainer.ACTION)
    }

    override fun getDetail(): String {
        val repeat = "${counterName}=${startIndex}; ${counterName}<=${endIndex}; ${counterName}+=${fluctuation}".replace("+=-", "-=")

        val details = mutableListOf("", "§7====§f for(${repeat}) §7====§f")
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
        val counterName = source.replaceVariables(counterName)

        val start = source.replaceVariables(startIndex)
        throwIfInvalidNumber(start)

        val end = source.replaceVariables(endIndex)
        throwIfInvalidNumber(end)

        val fluctuation = source.replaceVariables(fluctuation)
        throwIfInvalidNumber(fluctuation, exclude = listOf(0.0))

        for (i in start.toInt() until end.toInt() step fluctuation.toInt()) {
            yieldAll(FlowItemExecutor(getActions(), source.target, mutableMapOf(
                counterName to NumberVariable(i)
            ), source).executeGenerator())
    }
        source.resume()
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun hasCustomMenu(): Boolean {
        return true
    }

    override fun getCustomMenuButtons(): List<Button> {
        return listOf(
            Button("@action.edit") { player ->
                FlowItemContainerForm.sendActionList(player, this, FlowItemContainer.ACTION)
            },
            Button("@action.for.setting") { player ->
                sendCounterSetting(player)
            },
        )
    }

    fun sendCounterSetting(player: Player) {
        val action = this
        (CustomForm("@action.for.setting"))
            .setContents(mutableListOf(
                ExampleInput("@action.for.counterName", "i", counterName, true),
                ExampleNumberInput("@action.for.start", "0", startIndex, true),
                ExampleNumberInput("@action.for.end", "9", endIndex, true),
                ExampleNumberInput("@action.for.fluctuation", "1", fluctuation, true, null, null, listOf(0.0))
            )).onReceive { data ->
                counterName = data.getString(0)
                startIndex = data.getString(1)
                endIndex = data.getString(2)
                fluctuation = data.getString(3)
                FlowItemForm.sendFlowItemCustomMenu(player, action, FlowItemContainer.ACTION, listOf("@form.changed"))
            }.show(player)
    }

    @Suppress("UNCHECKED_CAST")
    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        for (content in contents[0] as List<Map<String, Any>>) {
            val action = loadEachSaveData(content)
            addItem(action, FlowItemContainer.ACTION)
        }

        counterName = contents.getString(1)
        startIndex = contents.getString(2)
        endIndex = contents.getString(3)
        fluctuation = contents.getString(4)
    }

    override fun serializeContents(): List<Any> {
        return listOf(
            getActions(),
            counterName,
            startIndex,
            endIndex,
            fluctuation,
        )
    }

    override fun getAddingVariables(): Map<String, DummyVariable<DummyVariable.Type>> {
        return mapOf(
            counterName to DummyVariable(DummyVariable.Type.NUMBER)
        )
    }

    override fun isDataValid(): Boolean {
        return true
    }

    override fun allowDirectCall(): Boolean {
        return false
    }

    override fun clone(): ForAction {
        val item = super.clone() as ForAction

        item.items = mutableMapOf()
        val actions = mutableListOf<FlowItem>()
        for (action in getActions()) {
            actions.add(action.clone())
        }
        setItems(actions.toMutableList(), FlowItemContainer.ACTION)

        return item
    }
}