package tokyo.aieuo.mineflow.flowItem.action.player

import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds

class AddXpLevel(player: String = "", xp: String = "") : AddXpProgress(player, xp) {

    override val id = FlowItemIds.ADD_XP_LEVEL

    override val nameTranslationKey = "action.addXpLevel.name"
    override val detailTranslationKey = "action.addXpLevel.detail"

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val xpStr = source.replaceVariables(xp)
        throwIfInvalidNumber(xpStr)
        var xp = xpStr.toInt()

        val player = getPlayer(source)
        throwIfInvalidPlayer(player)

        val new = player.experienceLevel + xp
        if (new < 0) xp = -player.experienceLevel
        player.sendExperienceLevel(player.experienceLevel + xp)
        yield(FlowItemExecutor.Result.CONTINUE)
    }
}