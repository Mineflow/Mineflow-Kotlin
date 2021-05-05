package tokyo.aieuo.mineflow.flowItem.action.world

import cn.nukkit.level.Position
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.PositionFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleNumberInput
import tokyo.aieuo.mineflow.formAPI.element.mineflow.PositionVariableDropdown
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.obj.PositionObjectVariable

class PositionVariableAddition(name: String = "pos", var x: String = "", var y: String = "", var z: String = "", var resultName: String = "pos"): FlowItem(), PositionFlowItem {

    override val id = FlowItemIds.POSITION_VARIABLE_ADDITION

    override val nameTranslationKey = "action.positionAddition.name"
    override val detailTranslationKey = "action.positionAddition.detail"
    override val detailDefaultReplaces = listOf("position", "x", "y", "z", "result")

    override val category = Category.WORLD

    override var positionVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setPositionVariableName(name)
    }

    override fun isDataValid(): Boolean {
        return getPositionVariableName() != "" && x != "" && y != "" && z != "" && resultName != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getPositionVariableName(), x, y, z, resultName))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val pos = getPosition(source)

        val x = source.replaceVariables(x)
        val y = source.replaceVariables(y)
        val z = source.replaceVariables(z)
        val name = source.replaceVariables(resultName)

        throwIfInvalidNumber(x)
        throwIfInvalidNumber(y)
        throwIfInvalidNumber(z)

        val position = Position.fromObject(pos.add(x.toDouble(), y.toDouble(), z.toDouble()), pos.level)

        val variable = PositionObjectVariable(position)
        source.addVariable(name, variable)
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            PositionVariableDropdown(variables),
            ExampleNumberInput("@action.positionAddition.form.x", "0", x, true),
            ExampleNumberInput("@action.positionAddition.form.y", "100", y, true),
            ExampleNumberInput("@action.positionAddition.form.z", "16", z, true),
            ExampleInput("@action.form.resultVariableName", "pos", resultName, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setPositionVariableName(contents.getString(0))
        x = contents.getString(1)
        y = contents.getString(2)
        z = contents.getString(3)
        resultName = contents.getString(4)
    }

    override fun serializeContents(): List<Any> {
        return listOf(getPositionVariableName(), x, y, z, resultName)
    }

    override fun getAddingVariables(): Map<String, DummyVariable<DummyVariable.Type>> {
        return mapOf(
            resultName to DummyVariable(DummyVariable.Type.POSITION, "${getPositionVariableName()} + ($x,$y,$z)")
        )
    }

    override fun clone(): PositionVariableAddition {
        val item = super.clone() as PositionVariableAddition
        item.positionVariableNames = positionVariableNames.toMutableMap()
        return item
    }
}