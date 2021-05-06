package tokyo.aieuo.mineflow.flowItem.action.world

import cn.nukkit.Server
import cn.nukkit.level.Position
import tokyo.aieuo.mineflow.exception.InvalidFlowValueException
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleNumberInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.obj.PositionObjectVariable

class CreatePositionVariable(
    var x: String = "",
    var y: String = "",
    var z: String = "",
    var level: String = "{target.world.name}",
    var variableName: String = "pos"
) : FlowItem() {

    override val id = FlowItemIds.CREATE_POSITION_VARIABLE

    override val nameTranslationKey = "action.createPositionVariable.name"
    override val detailTranslationKey = "action.createPositionVariable.detail"
    override val detailDefaultReplaces = listOf("position", "x", "y", "z", "world")

    override val category = Category.WORLD

    override fun isDataValid(): Boolean {
        return variableName != "" && x != "" && y != "" && z != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(variableName, x, y, z, level))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val name = source.replaceVariables(variableName)
        val x = source.replaceVariables(x)
        val y = source.replaceVariables(y)
        val z = source.replaceVariables(z)
        val levelName = source.replaceVariables(level)
        val level = Server.getInstance().getLevelByName(levelName)

        throwIfInvalidNumber(x)
        throwIfInvalidNumber(y)
        throwIfInvalidNumber(z)
        if (level === null) {
            throw InvalidFlowValueException(Language.get("action.createPositionVariable.world.notFound"))
        }

        val position = Position(x.toDouble(), y.toDouble(), z.toDouble(), level)

        val variable = PositionObjectVariable(position)
        source.addVariable(name, variable)
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            ExampleNumberInput("@action.createPositionVariable.form.x", "0", x, true),
            ExampleNumberInput("@action.createPositionVariable.form.y", "100", y, true),
            ExampleNumberInput("@action.createPositionVariable.form.z", "16", z, true),
            ExampleInput("@action.createPositionVariable.form.world", "{target.level}", level, true),
            ExampleInput("@action.form.resultVariableName", "pos", variableName, true),
        )
    }

    override fun parseFromFormData(data: CustomFormResponseList): List<Any?> {
        return listOf(data[4], data[0], data[1], data[2], data[3])
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        variableName = contents.getString(0)
        x = contents.getString(1)
        y = contents.getString(2)
        z = contents.getString(3)
        level = contents.getString(4)
    }

    override fun serializeContents(): List<Any> {
        return listOf(variableName, x, y, z, level)
    }

    override fun getAddingVariables(): DummyVariableMap {
        return mapOf(
            variableName to DummyVariable(DummyVariable.Type.POSITION, "$x, $y, $z, $level")
        )
    }
}