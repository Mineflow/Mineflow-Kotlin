package tokyo.aieuo.mineflow.flowItem.action.player

import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.PlayerFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Dropdown
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.PlayerVariableDropdown
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable

class SetGamemode(player: String = "", var gamemode: String = ""): FlowItem(), PlayerFlowItem {

    override val id = FlowItemIds.SET_GAMEMODE

    override val nameTranslationKey = "action.setGamemode.name"
    override val detailTranslationKey = "action.setGamemode.detail"
    override val detailDefaultReplaces = listOf("player", "gamemode")

    override val category = Category.PLAYER

    override var playerVariableNames: MutableMap<String, String> = mutableMapOf()

    private val gamemodes = listOf(
        "action.gamemode.survival",
        "action.gamemode.creative",
        "action.gamemode.adventure",
        "action.gamemode.spectator"
    )

    init {
        setPlayerVariableName(player)
    }

    override fun isDataValid(): Boolean {
        return getPlayerVariableName() != "" && gamemode != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getPlayerVariableName(), Language.get(gamemodes[gamemode.toInt()])))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        gamemode = source.replaceVariables(gamemode)
        throwIfInvalidNumber(gamemode, 0.0, 3.0)

        val player = getPlayer(source)
        throwIfInvalidPlayer(player)

        player.gamemode = gamemode.toInt()
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            PlayerVariableDropdown(variables, getPlayerVariableName()),
            Dropdown("@action.setGamemode.form.gamemode", gamemodes.map{ Language.get(it) }, gamemode.toInt()),
        )
    }

    override fun parseFromFormData(data: CustomFormResponseList): List<Any?> {
        return listOf(data[0], data[1].toString())
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setPlayerVariableName(contents.getString(0))
        gamemode = contents.getString(1)
    }

    override fun serializeContents(): List<Any> {
        return listOf(getPlayerVariableName(), gamemode)
    }

    override fun clone(): SetGamemode {
        val item = super.clone() as SetGamemode
        item.playerVariableNames = playerVariableNames.toMutableMap()
        return item
    }
}