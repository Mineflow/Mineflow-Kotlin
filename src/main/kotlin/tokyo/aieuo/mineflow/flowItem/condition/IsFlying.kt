package tokyo.aieuo.mineflow.flowItem.condition

import cn.nukkit.AdventureSettings
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.PlayerFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.PlayerVariableDropdown
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable

class IsFlying(player: String = ""): FlowItem(), Condition, PlayerFlowItem {

    override val id = FlowItemIds.IS_FLYING

    override val nameTranslationKey = "condition.isFlying.name"
    override val detailTranslationKey = "condition.isFlying.detail"
    override val detailDefaultReplaces = listOf("player")

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
        return Language.get(detailTranslationKey, listOf(getPlayerVariableName()))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val player = getPlayer(source)
        throwIfInvalidPlayer(player)

        val flying = player.adventureSettings.get(AdventureSettings.Type.FLYING)
        yield(if (flying) FlowItemExecutor.Result.SUCCESS else FlowItemExecutor.Result.FAILURE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            PlayerVariableDropdown(variables, getPlayerVariableName()),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        if (contents.isNotEmpty()) setPlayerVariableName(contents.getString(0))
    }

    override fun serializeContents(): List<Any> {
        return listOf(getPlayerVariableName())
    }

    override fun clone(): IsFlying {
        val item = super.clone() as IsFlying
        item.playerVariableNames = playerVariableNames.toMutableMap()
        return item
    }
}
