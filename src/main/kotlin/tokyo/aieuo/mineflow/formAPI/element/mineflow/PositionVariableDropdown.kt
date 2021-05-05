package tokyo.aieuo.mineflow.formAPI.element.mineflow

import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.variable.DummyVariable

class PositionVariableDropdown(
    variables: Map<String, DummyVariable<DummyVariable.Type>> = mapOf(),
    default: String = "",
    text: String = "@action.form.target.position",
    optional: Boolean = false
) : VariableDropdown(
    text,
    variables,
    listOf(
        DummyVariable.Type.POSITION,
        DummyVariable.Type.LOCATION,
        DummyVariable.Type.PLAYER,
        DummyVariable.Type.ENTITY,
        DummyVariable.Type.BLOCK
    ),
    default,
    optional
) {

    override val variableType = DummyVariable.Type.POSITION

    override val actions = listOf(
        FlowItemIds.CREATE_POSITION_VARIABLE,
        FlowItemIds.GET_ENTITY_SIDE,
    )

    init {
        updateOptions(flattenVariables(variables))
    }
}