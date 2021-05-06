package tokyo.aieuo.mineflow.flowItem.base


import cn.nukkit.Player
import tokyo.aieuo.mineflow.exception.InvalidFlowValueException
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.obj.PlayerObjectVariable

interface PlayerFlowItem {

    var playerVariableNames: MutableMap<String, String>

    fun getPlayerVariableName(name: String = ""): String {
        return playerVariableNames[name] ?: ""
    }

    fun setPlayerVariableName(player: String, name: String = "") {
        playerVariableNames[name] = player
    }

    fun getPlayer(source: FlowItemExecutor, name: String = ""): Player {
        val rawName = getPlayerVariableName(name)
        val player = source.replaceVariables(rawName)

        val variable = source.getVariable(player)
        if (variable is PlayerObjectVariable<*>) {
            return variable.value
        }

        throw InvalidFlowValueException(
            Language.get(
                "action.target.not.valid", listOf(
                    Language.get("action.target.require.player"),
                    rawName
                )
            )
        )
    }

    fun throwIfInvalidPlayer(player: Player, checkOnline: Boolean = true) {
        if (checkOnline && !player.isOnline) {
            throw InvalidFlowValueException(Language.get("action.error.player.offline"))
        }
    }
}