package tokyo.aieuo.mineflow.flowItem.condition

import cn.nukkit.utils.TextFormat
import tokyo.aieuo.mineflow.economy.Economy
import tokyo.aieuo.mineflow.exception.InvalidFlowValueException
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.utils.Language

class TakeMoneyCondition(playerName: String = "{target.name}", amount: String = "") : TypeMoney(playerName, amount) {

    override val id = FlowItemIds.TAKE_MONEY_CONDITION

    override val nameTranslationKey = "condition.takeMoney.name"
    override val detailTranslationKey = "condition.takeMoney.detail"

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        if (!Economy.isPluginLoaded()) {
            throw InvalidFlowValueException(TextFormat.RED.toString() + Language.get("economy.notfound"))
        }

        val name = source.replaceVariables(playerName)
        val amountStr = source.replaceVariables(amount)
        throwIfInvalidNumber(amountStr, 1.0)

        val amount = amountStr.toInt()

        val myMoney = Economy.plugin?.getMoney(name) ?: 0
        if (myMoney >= amount) {
            Economy.plugin?.takeMoney(name, amount)
            yield(FlowItemExecutor.Result.SUCCESS)
        } else {
            yield((FlowItemExecutor.Result.FAILURE))
        }
    }
}