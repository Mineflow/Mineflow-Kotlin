package tokyo.aieuo.mineflow.flowItem.action.script

import cn.nukkit.Player
import tokyo.aieuo.mineflow.flowItem.*
import tokyo.aieuo.mineflow.formAPI.CustomForm
import tokyo.aieuo.mineflow.formAPI.element.Button
import tokyo.aieuo.mineflow.formAPI.element.CancelToggle
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleNumberInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.ui.FlowItemContainerForm
import tokyo.aieuo.mineflow.ui.FlowItemForm
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.NumberVariable

class WhileTaskAction(conditions: List<FlowItem> = listOf(), actions: List<FlowItem> = listOf(), var interval: Int = 20, override var customName: String = ""): FlowItem(), FlowItemContainer {

    override val id = FlowItemIds.ACTION_WHILE_TASK

    override val nameTranslationKey = "action.whileTask.name"
    override val detailTranslationKey = "action.whileTask.description"

    override val category = Category.SCRIPT

    override val permission = PERMISSION_LEVEL_1

    private var limit = -1
    private val loopCount = 0

    override var items: MutableMap<String, MutableList<FlowItem>> = mutableMapOf()

    init {
        setItems(conditions.toMutableList(), FlowItemContainer.CONDITION)
        setItems(actions.toMutableList(), FlowItemContainer.ACTION)
    }

    override fun getDetail(): String {
        val details = mutableListOf("", "§7========§f whileTask($interval) §7========§f")
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
        val wait = Wait((interval / 20.0).toString())
        while (true) {
            source.addVariable("i", NumberVariable(loopCount)) // TODO: i を変更できるようにする
            for (condition in getConditions()) {
                for (result in condition.execute(source)) {
                    if (result == FlowItemExecutor.Result.FAILURE) {
                        source.resume()
                        yield(FlowItemExecutor.Result.SUCCESS)
                        return@sequence
                    }
                }
            }

            yieldAll(FlowItemExecutor(getActions(), source.target, parent = source).executeGenerator())
            yieldAll(wait.execute(source))
        }
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
            Button("@action.whileTask.editInterval") { player ->
                sendSetWhileIntervalForm(player)
            },
        )
    }

    fun sendSetWhileIntervalForm(player: Player) {
        val action = this
        (CustomForm("@action.repeat.editCount"))
            .setContents(mutableListOf(
                ExampleNumberInput("@action.whileTask.interval", "20", interval.toString(), true, 1.0),
                CancelToggle { FlowItemForm.sendFlowItemCustomMenu(player, this, FlowItemContainer.ACTION, listOf("@form.cancelled")) }
            )).onReceive { data ->
                interval = data.getInt(0)
                FlowItemForm.sendFlowItemCustomMenu(player, action, FlowItemContainer.ACTION, listOf("@form.changed"))
            }.show(player)
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

        interval = contents.getIntOrNull(2) ?: 20
        limit = contents.getIntOrNull(3) ?: -1
    }

    override fun serializeContents(): List<Any> {
        return listOf(
            getConditions(),
            getActions(),
            interval,
            limit,
        )
    }

    override fun getAddingVariables(): Map<String, DummyVariable<DummyVariable.Type>> {
        return mapOf(
            "i" to DummyVariable(DummyVariable.Type.NUMBER)
        )
    }

    override fun isDataValid(): Boolean {
        return true
    }

    override fun clone(): WhileTaskAction {
        val item = super.clone() as WhileTaskAction

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