package tokyo.aieuo.mineflow.variable.obj

import tokyo.aieuo.mineflow.utils.Scoreboard
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.NumberVariable
import tokyo.aieuo.mineflow.variable.ObjectVariable
import tokyo.aieuo.mineflow.variable.Variable

class ScoreboardObjectVariable(value: Scoreboard, showString: String? = null): ObjectVariable<Scoreboard>(value, showString) {

    override fun getValueFromIndex(index: String): Variable<Any>? {
        val scores = value.scores

        return NumberVariable(scores[index] ?: return null)
    }

    override fun toString(): String {
        return "ScoreboardVariable"
    }

    companion object {
        fun getValuesDummy(): Map<String, DummyVariable<DummyVariable.Type>> {
            return mapOf()
        }
    }
}