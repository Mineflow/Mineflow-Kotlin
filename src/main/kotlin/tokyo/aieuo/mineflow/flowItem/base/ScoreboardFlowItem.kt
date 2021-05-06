package tokyo.aieuo.mineflow.flowItem.base


import tokyo.aieuo.mineflow.exception.InvalidFlowValueException
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.utils.Scoreboard
import tokyo.aieuo.mineflow.variable.obj.ScoreboardObjectVariable

interface ScoreboardFlowItem {

    var scoreboardVariableNames: MutableMap<String, String>

    fun getScoreboardVariableName(name: String = ""): String {
        return scoreboardVariableNames[name] ?: ""
    }

    fun setScoreboardVariableName(scoreboard: String, name: String = "") {
        scoreboardVariableNames[name] = scoreboard
    }

    fun getScoreboard(source: FlowItemExecutor, name: String = ""): Scoreboard {
        val rawName = getScoreboardVariableName(name)
        val scoreboard = source.replaceVariables(rawName)

        val variable = source.getVariable(scoreboard)
        if (variable is ScoreboardObjectVariable) {
            return variable.value
        }

        throw InvalidFlowValueException(
            Language.get(
                "action.target.not.valid", listOf(
                    Language.get("action.target.require.scoreboard"),
                    rawName
                )
            )
        )
    }
}