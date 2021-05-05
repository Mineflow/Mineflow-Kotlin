package tokyo.aieuo.mineflow.formAPI.element.mineflow

import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.variable.DummyVariable

class ConfigVariableDropdown(
    variables: Map<String, DummyVariable<DummyVariable.Type>> = mapOf(),
    default: String = "",
    text: String = "@action.form.target.config",
    optional: Boolean = false
) : VariableDropdown(
    text,
    variables,
    listOf(DummyVariable.Type.CONFIG),
    default,
    optional
) {

    override val variableType = DummyVariable.Type.CONFIG

    override val actions = listOf(
        FlowItemIds.CREATE_CONFIG_VARIABLE,
    )

    init {
        updateOptions(flattenVariables(variables))
    }
}