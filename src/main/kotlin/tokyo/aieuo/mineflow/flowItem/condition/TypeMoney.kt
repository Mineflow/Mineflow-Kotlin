package tokyo.aieuo.mineflow.flowItem.condition

import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleNumberInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language

abstract class TypeMoney(var playerName: String = "{target.name}", var amount: String = "") : FlowItem(), Condition {

    override val detailDefaultReplaces = listOf("target", "amount")

    override val category = Category.PLUGIN

    override fun isDataValid(): Boolean {
        return playerName != "" && amount != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(playerName, amount))
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            ExampleInput("@action.money.form.target", "{target.name}", playerName, true),
            ExampleNumberInput("@action.money.form.amount", "1000", amount, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        playerName = contents.getString(0)
        amount = contents.getString(1)
    }

    override fun serializeContents(): List<Any> {
        return listOf(playerName, amount)
    }
}