package tokyo.aieuo.mineflow.flowItem.condition

import tokyo.aieuo.mineflow.economy.Economy
import tokyo.aieuo.mineflow.exception.InvalidFlowValueException
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import cn.nukkit.utils.TextFormat
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.utils.Language

class OverMoney(playerName: String = "{target.name}", amount: String = ""): TypeMoney(playerName, amount) {

    override val id = FlowItemIds.OVER_MONEY

    override val nameTranslationKey = "condition.overMoney.name"
    override val detailTranslationKey = "condition.overMoney.detail"

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        if (!Economy.isPluginLoaded()) {
            throw InvalidFlowValueException(TextFormat.RED.toString() + Language.get("economy.notfound"))
        }

        val name = source.replaceVariables(playerName)
        val amount = source.replaceVariables(amount)

        throwIfInvalidNumber(amount)

        val myMoney = Economy.plugin?.getMoney(name) ?: 0

        val result = myMoney >= amount.toInt()
        yield(if (result) FlowItemExecutor.Result.SUCCESS else FlowItemExecutor.Result.FAILURE)
    }
}