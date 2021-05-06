package tokyo.aieuo.mineflow.flowItem.action.player

import cn.nukkit.scheduler.NukkitRunnable
import tokyo.aieuo.mineflow.Main
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

class Kick(player: String = "", var reason: String = "", var isAdmin: Boolean = false) : FlowItem(), PlayerFlowItem {

    override val id = FlowItemIds.KICK

    override val nameTranslationKey = "action.kick.name"
    override val detailTranslationKey = "action.kick.detail"
    override val detailDefaultReplaces = listOf("player", "reason")

    override val category = Category.PLAYER

    override var playerVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setPlayerVariableName(player)
    }

    override fun isDataValid(): Boolean {
        return getPlayerVariableName() != "" && reason != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getPlayerVariableName(), reason))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val reason = source.replaceVariables(reason)

        val player = getPlayer(source)
        throwIfInvalidPlayer(player)

        object : NukkitRunnable() {
            override fun run() {
                player.kick(reason, isAdmin)
            }
        }.runTaskLater(Main.instance, 1)
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            PlayerVariableDropdown(variables, getPlayerVariableName()),
            ExampleInput("@action.kick.form.reason", "aieuo", reason),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setPlayerVariableName(contents.getString(0))
        reason = contents.getString(1)
        if (contents.size > 2 && contents[2] is Boolean) isAdmin = contents.getBoolean(2)
    }

    override fun serializeContents(): List<Any> {
        return listOf(getPlayerVariableName(), reason, isAdmin)
    }

    override fun clone(): Kick {
        val item = super.clone() as Kick
        item.playerVariableNames = playerVariableNames.toMutableMap()
        return item
    }
}