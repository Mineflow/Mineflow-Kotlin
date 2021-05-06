package tokyo.aieuo.mineflow.flowItem.condition

import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.PlayerFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.PlayerVariableDropdown
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language

class IsOp(player: String = "") : FlowItem(), Condition, PlayerFlowItem {

    override val id = FlowItemIds.IS_OP

    override val nameTranslationKey = "condition.isOp.name"
    override val detailTranslationKey = "condition.isOp.detail"
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

        yield(if (player.isOp) FlowItemExecutor.Result.SUCCESS else FlowItemExecutor.Result.FAILURE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
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

    override fun clone(): IsOp {
        val item = super.clone() as IsOp
        item.playerVariableNames = playerVariableNames.toMutableMap()
        return item
    }
}