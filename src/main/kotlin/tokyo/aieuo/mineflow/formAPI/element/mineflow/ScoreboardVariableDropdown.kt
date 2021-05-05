package tokyo.aieuo.mineflow.formAPI.element.mineflow

import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.variable.DummyVariable

class ScoreboardVariableDropdown(
    variables: Map<String, DummyVariable<DummyVariable.Type>> = mapOf(),
    default: String = "",
    text: String = "@action.form.target.scoreboard",
    optional: Boolean = false
) : VariableDropdown(
    text,
    variables,
    listOf(DummyVariable.Type.SCOREBOARD),
    default,
    optional
) {

    override val variableType = DummyVariable.Type.SCOREBOARD

    override val actions = listOf(
        FlowItemIds.CREATE_CONFIG_VARIABLE,
    )

    init {
        updateOptions(flattenVariables(variables))
    }
}