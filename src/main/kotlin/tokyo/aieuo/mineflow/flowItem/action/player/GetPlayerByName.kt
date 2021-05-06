package tokyo.aieuo.mineflow.flowItem.action.player

import cn.nukkit.Player
import cn.nukkit.Server
import tokyo.aieuo.mineflow.exception.InvalidFlowValueException
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.obj.PlayerObjectVariable

class GetPlayerByName(var playerName: String = "", var resultName: String = "player") : FlowItem() {

    override val id = FlowItemIds.GET_PLAYER

    override val nameTranslationKey = "action.getPlayerByName.name"
    override val detailTranslationKey = "action.getPlayerByName.detail"
    override val detailDefaultReplaces = listOf("name", "result")

    override val category = Category.PLAYER

    override fun isDataValid(): Boolean {
        return playerName != "" && resultName != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(playerName, resultName))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val name = source.replaceVariables(playerName)
        val resultName = source.replaceVariables(resultName)

        val player = Server.getInstance().getPlayer(name)
        if (player !is Player) {
            throw InvalidFlowValueException(Language.get("action.getPlayerByName.player.notFound"))
        }

        val result = PlayerObjectVariable(player, player.name)
        source.addVariable(resultName, result)
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            ExampleInput("@action.getPlayerByName.form.target", "aieuo", playerName, true),
            ExampleInput("@action.form.resultVariableName", "player", resultName, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        playerName = contents.getString(0)
        resultName = contents.getString(1)
    }

    override fun serializeContents(): List<Any> {
        return listOf(playerName, resultName)
    }

    override fun getAddingVariables(): DummyVariableMap {
        return mapOf(
            resultName to DummyVariable(DummyVariable.Type.PLAYER, playerName)
        )
    }
}