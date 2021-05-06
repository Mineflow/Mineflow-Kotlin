package tokyo.aieuo.mineflow.flowItem.action.script

import cn.nukkit.Player
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemContainer
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.formAPI.CustomForm
import tokyo.aieuo.mineflow.formAPI.element.Button
import tokyo.aieuo.mineflow.formAPI.element.CancelToggle
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleNumberInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.ui.FlowItemContainerForm
import tokyo.aieuo.mineflow.ui.FlowItemForm
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.NumberVariable

class RepeatAction(actions: List<FlowItem> = listOf(), var repeatCount: String = "1") : FlowItem(), FlowItemContainer {

    override val id = FlowItemIds.ACTION_REPEAT

    override val nameTranslationKey = "action.repeat.name"
    override val detailTranslationKey = "action.repeat.description"

    override val category = Category.SCRIPT

    override val permission = PERMISSION_LEVEL_1

    override var items: MutableMap<String, MutableList<FlowItem>> = mutableMapOf()

    var startIndex = "0"
    var counterName = "i"

    init {
        setItems(actions.toMutableList(), FlowItemContainer.ACTION)
    }

    override fun getDetail(): String {
        val details = mutableListOf("", "§7========= §frepeat($repeatCount)§7 =========§f")
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
        val count = source.replaceVariables(repeatCount)
        throwIfInvalidNumber(count, 1.0)

        val start = source.replaceVariables(startIndex)
        throwIfInvalidNumber(start)

        val end = start.toInt() + count.toInt()

        for (i in start.toInt() until end) {
            yieldAll(
                FlowItemExecutor(
                    getActions(), source.target, mutableMapOf(
                        counterName to NumberVariable(i)
                    ), source
                ).executeGenerator()
            )
        }
        source.resume()
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
                sendSetRepeatCountForm(player)
            },
        )
    }

    fun sendSetRepeatCountForm(player: Player) {
        val action = this
        (CustomForm("@action.repeat.editCount"))
            .setContents(mutableListOf(
                ExampleNumberInput("@action.repeat.repeatCount", "10", repeatCount, true, 1.0),
                CancelToggle {
                    FlowItemForm.sendFlowItemCustomMenu(
                        player,
                        this,
                        FlowItemContainer.ACTION,
                        listOf("@form.cancelled")
                    )
                }
            )).onReceive { data ->
                repeatCount = data.getString(0)
                FlowItemForm.sendFlowItemCustomMenu(player, action, FlowItemContainer.ACTION, listOf("@form.changed"))
            }.show(player)
    }

    @Suppress("UNCHECKED_CAST")
    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        repeatCount = contents.getString(0)

        for (content in contents[1] as List<Map<String, Any>>) {
            val action = loadEachSaveData(content)
            addItem(action, FlowItemContainer.ACTION)
        }

        if (contents.size > 2) startIndex = contents.getString(2)
        if (contents.size > 3) counterName = contents.getString(3)
    }

    override fun serializeContents(): List<Any> {
        return listOf(
            repeatCount,
            getActions(),
            startIndex,
            counterName
        )
    }

    override fun getAddingVariables(): DummyVariableMap {
        return mapOf(
            counterName to DummyVariable(DummyVariable.Type.NUMBER)
        )
    }

    override fun isDataValid(): Boolean {
        return true
    }

    override fun clone(): RepeatAction {
        val item = super.clone() as RepeatAction

        item.items = mutableMapOf()
        val actions = mutableListOf<FlowItem>()
        for (action in getActions()) {
            actions.add(action.clone())
        }
        item.setItems(actions, FlowItemContainer.ACTION)

        return item
    }
}