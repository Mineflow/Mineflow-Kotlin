package tokyo.aieuo.mineflow.formAPI.element.mineflow

import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.variable.DummyVariable

class PlayerVariableDropdown(
    variables: DummyVariableMap = mapOf(),
    default: String = "",
    text: String = "@action.form.target.player",
    optional: Boolean = false
) : VariableDropdown(
    text,
    variables,
    listOf(DummyVariable.Type.PLAYER),
    default,
    optional
) {

    override val variableType = DummyVariable.Type.PLAYER

    override val actions = listOf(
        FlowItemIds.GET_PLAYER
    )

    init {
        updateOptions(flattenVariables(variables))
    }
}