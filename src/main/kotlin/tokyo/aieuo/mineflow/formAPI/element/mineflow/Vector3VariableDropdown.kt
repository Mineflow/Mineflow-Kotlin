package tokyo.aieuo.mineflow.formAPI.element.mineflow

import tokyo.aieuo.mineflow.variable.DummyVariable

class Vector3VariableDropdown(
    variables: Map<String, DummyVariable<DummyVariable.Type>> = mapOf(),
    default: String = "",
    text: String = "@action.form.target.position",
    optional: Boolean = false
) : VariableDropdown(
    text,
    variables,
    listOf(
        DummyVariable.Type.VECTOR3,
        DummyVariable.Type.POSITION,
        DummyVariable.Type.LOCATION,
        DummyVariable.Type.PLAYER,
        DummyVariable.Type.ENTITY,
        DummyVariable.Type.BLOCK
    ),
    default,
    optional
) {

    override val variableType = DummyVariable.Type.VECTOR3

    init {
        updateOptions(flattenVariables(variables))
    }
}