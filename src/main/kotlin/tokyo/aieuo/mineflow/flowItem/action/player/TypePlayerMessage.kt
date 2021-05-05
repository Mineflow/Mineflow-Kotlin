package tokyo.aieuo.mineflow.flowItem.action.player

import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.base.PlayerFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.element.mineflow.PlayerVariableDropdown
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable

@Suppress("LeakingThis")
abstract class TypePlayerMessage(player: String = "", var message: String = ""): FlowItem(), PlayerFlowItem {

    override val detailDefaultReplaces = listOf("player", "message")

    override val category = Category.PLAYER

    override var playerVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setPlayerVariableName(player)
    }

    override fun isDataValid(): Boolean {
        return getPlayerVariableName() != "" && message != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getPlayerVariableName(), message))
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            PlayerVariableDropdown(variables, getPlayerVariableName()),
            ExampleInput("@action.message.form.message", "aieuo", message, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setPlayerVariableName(contents.getString(0))
        message = contents.getString(1)
    }

    override fun serializeContents(): List<Any> {
        return listOf(getPlayerVariableName(), message)
    }

    override fun clone(): TypePlayerMessage {
        val item = super.clone() as TypePlayerMessage
        item.playerVariableNames = playerVariableNames.toMutableMap()
        return item
    }
}