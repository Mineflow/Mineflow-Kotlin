package tokyo.aieuo.mineflow.formAPI.element.mineflow

import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.variable.DummyVariable

class ItemVariableDropdown(
    variables: Map<String, DummyVariable<DummyVariable.Type>> = mapOf(),
    default: String = "",
    text: String = "@action.form.target.item",
    optional: Boolean = false
) : VariableDropdown(
    text,
    variables,
    listOf(DummyVariable.Type.ITEM),
    default,
    optional
) {

    override val variableType = DummyVariable.Type.ITEM

    override val actions = listOf(
        FlowItemIds.CREATE_ITEM_VARIABLE,
    )

    init {
        updateOptions(flattenVariables(variables))
    }
}