package tokyo.aieuo.mineflow.formAPI.element.mineflow

import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.variable.DummyVariable

class BlockVariableDropdown(
    variables: DummyVariableMap = mapOf(),
    default: String = "",
    text: String = "@action.form.target.block",
    optional: Boolean = false
) : VariableDropdown(
    text,
    variables,
    listOf(DummyVariable.Type.BLOCK),
    default,
    optional
) {

    override val variableType = DummyVariable.Type.BLOCK

    override val actions = listOf(
        FlowItemIds.CREATE_BLOCK_VARIABLE,
        FlowItemIds.GET_BLOCK,
        FlowItemIds.GET_TARGET_BLOCK,
    )

    init {
        updateOptions(flattenVariables(variables))
    }
}