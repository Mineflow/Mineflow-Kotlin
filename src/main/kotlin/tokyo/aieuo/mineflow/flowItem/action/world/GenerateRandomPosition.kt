package tokyo.aieuo.mineflow.flowItem.action.world

import cn.nukkit.level.Position
import tokyo.aieuo.mineflow.exception.InvalidFlowValueException
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.PositionFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.element.mineflow.PositionVariableDropdown
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.obj.PositionObjectVariable
import kotlin.math.max
import kotlin.math.min

class GenerateRandomPosition(min: String = "", max: String = "", var resultName: String = "position") :
    FlowItem(), PositionFlowItem {

    override val id = FlowItemIds.GENERATE_RANDOM_POSITION

    override val nameTranslationKey = "action.generateRandomPosition.name"
    override val detailTranslationKey = "action.generateRandomPosition.detail"
    override val detailDefaultReplaces = listOf("min", "max", "result")

    override var positionVariableNames: MutableMap<String, String> = mutableMapOf()

    override val category = Category.WORLD

    init {
        setPositionVariableName(min, "pos1")
        setPositionVariableName(max, "pos2")
    }

    override fun isDataValid(): Boolean {
        return resultName != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(
            detailTranslationKey,
            listOf(getPositionVariableName("pos1"), getPositionVariableName("pos2"), resultName)
        )
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val pos1 = getPosition(source, "pos1")
        val pos2 = getPosition(source, "pos2")
        val resultName = source.replaceVariables(resultName)

        if (pos1.level.folderName !== pos2.level.folderName) {
            throw InvalidFlowValueException(Language.get("action.position.world.different"))
        }

        val x = (min(pos1.x, pos2.x).toInt()..max(pos1.x, pos2.x).toInt()).random()
        val y = (min(pos1.y, pos2.y).toInt()..max(pos1.y, pos2.y).toInt()).random()
        val z = (min(pos1.z, pos2.z).toInt()..max(pos1.z, pos2.z).toInt()).random()
        val rand = Position(x.toDouble(), y.toDouble(), z.toDouble(), pos1.level)
        source.addVariable(resultName, PositionObjectVariable(rand))
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            PositionVariableDropdown(variables, getPositionVariableName("pos1"), "@action.form.target.position 1"),
            PositionVariableDropdown(variables, getPositionVariableName("pos2"), "@action.form.target.position 2"),
            ExampleInput("@action.form.resultVariableName", "position", resultName, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setPositionVariableName(contents.getString(0), "pos1")
        setPositionVariableName(contents.getString(1), "pos2")
        resultName = contents.getString(2)
    }

    override fun serializeContents(): List<Any> {
        return listOf(getPositionVariableName("pos1"), getPositionVariableName("pos2"), resultName)
    }

    override fun getAddingVariables(): DummyVariableMap {
        return mapOf(
            resultName to DummyVariable(DummyVariable.Type.POSITION)
        )
    }

    override fun clone(): GenerateRandomPosition {
        val item = super.clone() as GenerateRandomPosition
        item.positionVariableNames = positionVariableNames.toMutableMap()
        return item
    }
}