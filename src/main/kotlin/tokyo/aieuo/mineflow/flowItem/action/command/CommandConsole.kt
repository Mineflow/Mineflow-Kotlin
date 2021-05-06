package tokyo.aieuo.mineflow.flowItem.action.command

import cn.nukkit.Server
import tokyo.aieuo.mineflow.command.MineflowConsoleCommandSender
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language

class CommandConsole(var command: String = "") : FlowItem() {

    override val id = FlowItemIds.COMMAND_CONSOLE

    override val nameTranslationKey = "action.commandConsole.name"
    override val detailTranslationKey = "action.commandConsole.detail"
    override val detailDefaultReplaces = listOf("command")

    override val category = Category.COMMAND

    override val permission = PERMISSION_LEVEL_1

    override fun isDataValid(): Boolean {
        return command != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(command))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val command = source.replaceVariables(command)

        Server.getInstance().dispatchCommand(MineflowConsoleCommandSender(), command)
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            ExampleInput("@action.command.form.command", "mineflow", command, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        command = contents.getString(0)
    }

    override fun serializeContents(): List<Any> {
        return listOf(command)
    }
}