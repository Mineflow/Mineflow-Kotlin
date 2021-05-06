package tokyo.aieuo.mineflow.flowItem.action.world

import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.PositionFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.NumberVariable

class GetDistance(pos1: String = "", pos2: String = "", var resultName: String = "distance") :
    FlowItem(), PositionFlowItem {

    override val id = FlowItemIds.GET_DISTANCE

    override val nameTranslationKey = "action.getDistance.name"
    override val detailTranslationKey = "action.getDistance.detail"
    override val detailDefaultReplaces = listOf("pos1", "pos2", "result")

    override val category = Category.WORLD

    override var positionVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setPositionVariableName(pos1, "pos1")
        setPositionVariableName(pos2, "pos2")
    }

    override fun isDataValid(): Boolean {
        return getPositionVariableName("pos1") != "" && getPositionVariableName("pos2") != "" && resultName != ""
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
        val result = source.replaceVariables(resultName)

        val distance = pos1.distance(pos2)

        source.addVariable(result, NumberVariable(distance))
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            ExampleInput("@action.getDistance.form.pos1", "pos1", getPositionVariableName("pos1"), true),
            ExampleInput("@action.getDistance.form.pos2", "pos2", getPositionVariableName("pos2"), true),
            ExampleInput("@action.form.resultVariableName", "distance", resultName, true),
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
            resultName to DummyVariable(DummyVariable.Type.NUMBER)
        )
    }

    override fun clone(): GetDistance {
        val item = super.clone() as GetDistance
        item.positionVariableNames = positionVariableNames.toMutableMap()
        return item
    }
}