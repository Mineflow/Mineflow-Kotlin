package tokyo.aieuo.mineflow.flowItem.action.plugin

import cn.nukkit.utils.TextFormat
import tokyo.aieuo.mineflow.economy.Economy
import tokyo.aieuo.mineflow.exception.InvalidFlowValueException
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.NumberVariable

class GetMoney(var playerName: String = "{target.name}", var resultName: String = "money"): FlowItem() {

    override val id = FlowItemIds.GET_MONEY

    override val nameTranslationKey = "action.getMoney.name"
    override val detailTranslationKey = "action.getMoney.detail"
    override val detailDefaultReplaces = listOf("target", "result")

    override val category = Category.PLUGIN

    override fun isDataValid(): Boolean {
        return playerName != "" && resultName != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(playerName, resultName))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        if (!Economy.isPluginLoaded()) {
            throw InvalidFlowValueException(TextFormat.RED.toString() + Language.get("economy.notfound"))
        }

        val targetName = source.replaceVariables(playerName)
        val resultName = source.replaceVariables(resultName)

        Economy.plugin?.getMoney(targetName)?.let { source.addVariable(resultName, NumberVariable(it)) }
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            ExampleInput("@action.money.form.target", "{target.name}", playerName, true),
            ExampleInput("@action.form.resultVariableName", "money", resultName, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        playerName = contents.getString(0)
        resultName = contents.getString(1)
    }

    override fun serializeContents(): List<Any> {
        return listOf(playerName, resultName)
    }

    override fun getAddingVariables(): Map<String, DummyVariable<DummyVariable.Type>> {
        return mapOf(
            resultName to DummyVariable(DummyVariable.Type.NUMBER)
        )
    }
}