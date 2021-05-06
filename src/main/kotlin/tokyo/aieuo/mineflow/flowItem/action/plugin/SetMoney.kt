package tokyo.aieuo.mineflow.flowItem.action.plugin

import cn.nukkit.utils.TextFormat
import tokyo.aieuo.mineflow.economy.Economy
import tokyo.aieuo.mineflow.exception.InvalidFlowValueException
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.utils.Language

class SetMoney(playerName: String = "{target.name}", amount: String = "") : TypeMoney(playerName, amount) {

    override val id = FlowItemIds.SET_MONEY

    override val nameTranslationKey = "action.setMoney.name"
    override val detailTranslationKey = "action.setMoney.detail"

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        if (!Economy.isPluginLoaded()) {
            throw InvalidFlowValueException(TextFormat.RED.toString() + Language.get("economy.notfound"))
        }

        val name = source.replaceVariables(playerName)
        val amount = source.replaceVariables(amount)

        throwIfInvalidNumber(amount, 1.0)

        Economy.plugin?.setMoney(name, amount.toInt())
        yield(FlowItemExecutor.Result.CONTINUE)
    }
}