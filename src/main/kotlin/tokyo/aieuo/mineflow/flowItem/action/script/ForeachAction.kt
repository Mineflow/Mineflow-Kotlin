package tokyo.aieuo.mineflow.flowItem.action.script

import cn.nukkit.Player
import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.exception.InvalidFlowValueException
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemContainer
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.formAPI.CustomForm
import tokyo.aieuo.mineflow.formAPI.element.Button
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.ui.FlowItemContainerForm
import tokyo.aieuo.mineflow.ui.FlowItemForm
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.*

class ForeachAction(actions: List<FlowItem> = listOf(), override var customName: String = "") :
    FlowItem(), FlowItemContainer {

    override val id = FlowItemIds.ACTION_FOREACH

    override val nameTranslationKey = "action.foreach.name"
    override val detailTranslationKey = "action.foreach.description"

    override val category = Category.SCRIPT

    override val permission = PERMISSION_LEVEL_1

    override var items: MutableMap<String, MutableList<FlowItem>> = mutableMapOf()

    var listVariableName = "list"
    var keyVariableName: String = "key"
    var valueVariableName = "value"

    init {
        setItems(actions.toMutableList(), FlowItemContainer.ACTION)
    }

    override fun getDetail(): String {
        val repeat = "$listVariableName as $keyVariableName => $valueVariableName"

        val details = mutableListOf("", "§7==§f foreach($repeat) §7==§f")
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
        val listName = source.replaceVariables(listVariableName)
        val list = source.getVariable(listName) ?: Main.variableHelper.getNested(listName)
        val keyName = source.replaceVariables(keyVariableName)
        val valueName = source.replaceVariables(valueVariableName)

        val value = when (list) {
            is ListVariable -> list.value.mapIndexed { i, v -> NumberVariable(i) to v }.toMap()
            is MapVariable -> list.value.mapKeys { StringVariable(it.key) }
            else -> throw InvalidFlowValueException(Language.get("action.foreach.error.notVariable", listOf(listName)))
        }

        for ((keyVariable, valueVariable) in value) {

            yieldAll(
                FlowItemExecutor(
                    getActions(), source.target, mutableMapOf(
                        keyName to keyVariable,
                        valueName to valueVariable
                    ), source
                ).executeGenerator()
            )
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
                sendSettingCounter(player)
            },
        )
    }

    fun sendSettingCounter(player: Player) {
        val action = this
        (CustomForm("@action.for.setting"))
            .setContents(
                mutableListOf(
                    ExampleInput("@action.foreach.listVariableName", "list", listVariableName, true),
                    ExampleInput("@action.foreach.keyVariableName", "key", keyVariableName, true),
                    ExampleInput("@action.foreach.valueVariableName", "value", valueVariableName, true),
                )
            ).onReceive { data ->
                listVariableName = data.getString(0)
                keyVariableName = data.getString(1)
                valueVariableName = data.getString(2)
                FlowItemForm.sendFlowItemCustomMenu(player, action, FlowItemContainer.ACTION, listOf("@form.changed"))
            }.show(player)
    }

    @Suppress("UNCHECKED_CAST")
    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        for (content in contents[0] as List<Map<String, Any>>) {
            val action = loadEachSaveData(content)
            addItem(action, FlowItemContainer.ACTION)
        }

        listVariableName = contents.getString(1)
        keyVariableName = contents.getString(2)
        valueVariableName = contents.getString(3)
    }

    override fun getAddingVariables(): DummyVariableMap {
        return mapOf(
            keyVariableName to DummyVariable(DummyVariable.Type.UNKNOWN),
            valueVariableName to DummyVariable(DummyVariable.Type.UNKNOWN),
        )
    }

    override fun serializeContents(): List<Any> {
        return listOf(getActions(), listVariableName, keyVariableName, valueVariableName)
    }

    override fun isDataValid(): Boolean {
        return true
    }

    override fun clone(): ForeachAction {
        val item = super.clone() as ForeachAction

        item.items = mutableMapOf()
        val actions = mutableListOf<FlowItem>()
        for (action in getActions()) {
            actions.add(action.clone())
        }
        item.setItems(actions, FlowItemContainer.ACTION)

        return item
    }
}