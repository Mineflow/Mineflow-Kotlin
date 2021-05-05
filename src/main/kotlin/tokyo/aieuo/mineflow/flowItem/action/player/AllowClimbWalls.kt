package tokyo.aieuo.mineflow.flowItem.action.player

import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.PlayerFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.Toggle
import tokyo.aieuo.mineflow.formAPI.element.mineflow.PlayerVariableDropdown
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable

class AllowClimbWalls(player: String = "", var allow: Boolean = true): FlowItem(), PlayerFlowItem {

    override val id = FlowItemIds.ALLOW_CLIMB_WALLS

    override val nameTranslationKey = "action.canClimbWalls.name"
    override val detailTranslationKey = "action.canClimbWalls.detail"
    override val detailDefaultReplaces = listOf("player", "allow")

    override val category = Category.PLAYER

    override var playerVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setPlayerVariableName(player)
    }

    override fun isDataValid(): Boolean {
        return getPlayerVariableName() != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getPlayerVariableName(), Language.get("action.allowFlight.${if (allow) "allow" else "notAllow"}")))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val player = getPlayer(source)
        throwIfInvalidPlayer(player)

        player.setCanClimbWalls(allow)
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            PlayerVariableDropdown(variables, getPlayerVariableName()),
            Toggle("@action.canClimbWalls.form.allow", allow),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setPlayerVariableName(contents.getString(0))
        allow = contents.getBoolean(1)
    }

    override fun serializeContents(): List<Any> {
        return listOf(getPlayerVariableName(), allow)
    }

    override fun clone(): AllowClimbWalls {
        val item = super.clone() as AllowClimbWalls
        item.playerVariableNames = playerVariableNames.toMutableMap()
        return item
    }
}