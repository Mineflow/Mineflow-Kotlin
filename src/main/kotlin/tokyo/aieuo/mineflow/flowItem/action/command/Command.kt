package tokyo.aieuo.mineflow.flowItem.action.command

import cn.nukkit.Server
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.PlayerFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.element.mineflow.PlayerVariableDropdown
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language

class Command(player: String = "", var command: String = "") : FlowItem(), PlayerFlowItem {

    override val id = FlowItemIds.COMMAND

    override val nameTranslationKey = "action.command.name"
    override val detailTranslationKey = "action.command.detail"
    override val detailDefaultReplaces = listOf("player", "command")

    override val category = Category.COMMAND

    override var playerVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setPlayerVariableName(player)
    }

    override fun isDataValid(): Boolean {
        return getPlayerVariableName() != "" && command != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getPlayerVariableName(), command))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val command = source.replaceVariables(command)

        val player = getPlayer(source)
        throwIfInvalidPlayer(player)

        Server.getInstance().dispatchCommand(player, command)
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            PlayerVariableDropdown(variables, getPlayerVariableName()),
            ExampleInput("@action.command.form.command", "command", command, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setPlayerVariableName(contents.getString(0))
        command = contents.getString(1)
    }

    override fun serializeContents(): List<Any> {
        return listOf(getPlayerVariableName(), command)
    }

    override fun clone(): Command {
        val item = super.clone() as Command
        item.playerVariableNames = playerVariableNames.toMutableMap()
        return item
    }
}