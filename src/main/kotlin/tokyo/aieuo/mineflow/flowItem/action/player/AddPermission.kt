package tokyo.aieuo.mineflow.flowItem.action.player

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

class AddPermission(player: String = "", var playerPermission: String = "") : FlowItem(), PlayerFlowItem {

    override val id = FlowItemIds.ADD_PERMISSION

    override val nameTranslationKey = "action.addPermission.name"
    override val detailTranslationKey = "action.addPermission.detail"
    override val detailDefaultReplaces = listOf("player", "permission")

    override val category = Category.PLAYER

    override val permission = PERMISSION_LEVEL_1

    override var playerVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setPlayerVariableName(player)
    }

    override fun isDataValid(): Boolean {
        return getPlayerVariableName() != "" && playerPermission != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getPlayerVariableName(), playerPermission))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val permission = source.replaceVariables(playerPermission)

        val player = getPlayer(source)
        throwIfInvalidPlayer(player)

        player.addAttachment(Main.instance, permission, true)
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            PlayerVariableDropdown(variables, getPlayerVariableName()),
            ExampleInput(
                "@condition.hasPermission.form.permission",
                "mineflow.customcommand.op",
                playerPermission,
                true
            ),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setPlayerVariableName(contents.getString(0))
        playerPermission = contents.getString(1)
    }

    override fun serializeContents(): List<Any> {
        return listOf(getPlayerVariableName(), playerPermission)
    }

    override fun clone(): AddPermission {
        val item = super.clone() as AddPermission
        item.playerVariableNames = playerVariableNames.toMutableMap()
        return item
    }
}