package tokyo.aieuo.mineflow.flowItem.action.player

import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.PlayerFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleNumberInput
import tokyo.aieuo.mineflow.formAPI.element.mineflow.PlayerVariableDropdown
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language

@Suppress("LeakingThis")
open class AddXpProgress(player: String = "", var xp: String = "") : FlowItem(), PlayerFlowItem {

    override val id = FlowItemIds.ADD_XP_PROGRESS

    override val nameTranslationKey = "action.addXp.name"
    override val detailTranslationKey = "action.addXp.detail"
    override val detailDefaultReplaces = listOf("player", "value")

    override val category = Category.PLAYER

    override var playerVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setPlayerVariableName(player)
    }

    override fun isDataValid(): Boolean {
        return xp != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getPlayerVariableName(), xp))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val xpStr = source.replaceVariables(xp)
        throwIfInvalidNumber(xp)
        var xp = xpStr.toInt()

        val player = getPlayer(source)
        throwIfInvalidPlayer(player)

        val new = player.experience + xp
        if (new < 0) xp = -player.experience
        player.addExperience(xp)
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            PlayerVariableDropdown(variables, getPlayerVariableName()),
            ExampleNumberInput("@action.addXp.form.xp", "10", xp, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setPlayerVariableName(contents.getString(0))
        xp = contents.getString(1)
    }

    override fun serializeContents(): List<Any> {
        return listOf(getPlayerVariableName(), xp)
    }

    override fun clone(): AddXpProgress {
        val item = super.clone() as AddXpProgress
        item.playerVariableNames = playerVariableNames.toMutableMap()
        return item
    }
}