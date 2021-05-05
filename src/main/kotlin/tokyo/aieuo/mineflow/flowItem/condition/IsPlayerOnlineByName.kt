package tokyo.aieuo.mineflow.flowItem.condition

import cn.nukkit.Player
import cn.nukkit.Server
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable

class IsPlayerOnlineByName(var playerName: String = "target"): FlowItem(), Condition {

    override val id = FlowItemIds.IS_PLAYER_ONLINE_BY_NAME

    override val nameTranslationKey = "condition.isPlayerOnlineByName.name"
    override val detailTranslationKey = "condition.isPlayerOnlineByName.detail"
    override val detailDefaultReplaces = listOf("player")

    override val category = Category.PLAYER

    override fun isDataValid(): Boolean {
        return playerName != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(playerName))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val name = source.replaceVariables(playerName)

        val player = Server.getInstance().getPlayerExact(name)

        val result = player is Player
        yield(if (result) FlowItemExecutor.Result.SUCCESS else FlowItemExecutor.Result.FAILURE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            ExampleInput("@condition.isPlayerOnline.form.name", "target", playerName, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        playerName = contents.getString(0)
    }

    override fun serializeContents(): List<Any> {
        return listOf(playerName)
    }
}