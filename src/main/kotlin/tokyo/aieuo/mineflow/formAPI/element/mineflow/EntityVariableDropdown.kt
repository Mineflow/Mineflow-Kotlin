package tokyo.aieuo.mineflow.formAPI.element.mineflow

import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.variable.DummyVariable

class EntityVariableDropdown(
    variables: Map<String, DummyVariable<DummyVariable.Type>> = mapOf(),
    default: String = "",
    text: String = "@action.form.target.entity",
    optional: Boolean = false
) : VariableDropdown(
    text,
    variables,
    listOf(DummyVariable.Type.PLAYER, DummyVariable.Type.ENTITY),
    default,
    optional
) {

    override val variableType = DummyVariable.Type.ENTITY

    override val actions = listOf(
        FlowItemIds.GET_ENTITY,
        FlowItemIds.CREATE_HUMAN_ENTITY,
    )

    init {
        updateOptions(flattenVariables(variables))
    }
}