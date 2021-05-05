package tokyo.aieuo.mineflow.flowItem.condition

import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.PlayerFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.element.mineflow.PlayerVariableDropdown
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable

class HasPermission(player: String = "", var playerPermission: String = ""): FlowItem(), Condition, PlayerFlowItem {

    override val id = FlowItemIds.HAS_PERMISSION

    override val nameTranslationKey = "condition.hasPermission.name"
    override val detailTranslationKey = "condition.hasPermission.detail"
    override val detailDefaultReplaces = listOf("player", "permission")

    override val category = Category.PLAYER

    override var playerVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setPlayerVariableName(player)
    }

    override fun isDataValid(): Boolean {
        return getPlayerVariableName() != "" && playerPermission != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(
            getPlayerVariableName(),
            playerPermission
        ))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val player = getPlayer(source)
        throwIfInvalidPlayer(player)

        val permission = playerPermission

        val result = player.hasPermission(permission)
        yield(if (result) FlowItemExecutor.Result.SUCCESS else FlowItemExecutor.Result.FAILURE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            PlayerVariableDropdown(variables, getPlayerVariableName()),
            ExampleInput("@condition.hasPermission.form.permission", "mineflow.customcommand.op", playerPermission, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setPlayerVariableName(contents.getString(0))
        playerPermission = contents.getString(1)
    }

    override fun serializeContents(): List<Any> {
        return listOf(getPlayerVariableName(), playerPermission)
    }

    override fun clone(): HasPermission {
        val item = super.clone() as HasPermission
        item.playerVariableNames = playerVariableNames.toMutableMap()
        return item
    }
}