package tokyo.aieuo.mineflow.flowItem.action.world

import cn.nukkit.level.Position
import cn.nukkit.math.BlockFace
import tokyo.aieuo.mineflow.exception.InvalidFlowValueException
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.EntityFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Dropdown
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.EntityVariableDropdown
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleNumberInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.obj.PositionObjectVariable

class GetEntitySidePosition(
    entity: String = "",
    var direction: String = "",
    var steps: String = "1",
    var resultName: String = "pos"
) : FlowItem(), EntityFlowItem {

    override val id = FlowItemIds.GET_ENTITY_SIDE

    override val nameTranslationKey = "action.getEntitySide.name"
    override val detailTranslationKey = "action.getEntitySide.detail"
    override val detailDefaultReplaces = listOf("entity", "direction", "step", "result")

    override val category = Category.WORLD

    override var entityVariableNames: MutableMap<String, String> = mutableMapOf()

    companion object {
        const val SIDE_DOWN = "down"
        const val SIDE_UP = "up"
        const val SIDE_NORTH = "north"
        const val SIDE_SOUTH = "south"
        const val SIDE_WEST = "west"
        const val SIDE_EAST = "east"
        const val SIDE_FRONT = "front"
        const val SIDE_BEHIND = "behind"
        const val SIDE_LEFT = "left"
        const val SIDE_RIGHT = "right"
    }

    private val directions = listOf(
        SIDE_DOWN,
        SIDE_UP,
        SIDE_NORTH,
        SIDE_SOUTH,
        SIDE_WEST,
        SIDE_EAST,
        SIDE_FRONT,
        SIDE_BEHIND,
        SIDE_LEFT,
        SIDE_RIGHT,
    )

    private val vector3SideMap = mapOf(
        // TODO: check
        SIDE_DOWN to BlockFace.DOWN,
        SIDE_UP to BlockFace.UP,
        SIDE_NORTH to BlockFace.NORTH,
        SIDE_SOUTH to BlockFace.SOUTH,
        SIDE_WEST to BlockFace.WEST,
        SIDE_EAST to BlockFace.EAST,
    )

    private val directionSideMap = listOf(
        BlockFace.EAST,
        BlockFace.SOUTH,
        BlockFace.WEST,
        BlockFace.NORTH,
    )

    init {
        setEntityVariableName(entity)
    }

    override fun isDataValid(): Boolean {
        return getEntityVariableName() != "" && direction != "" && steps != "" && resultName != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getEntityVariableName(), direction, steps, resultName))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val entity = getEntity(source)
        throwIfInvalidEntity(entity)

        val side = source.replaceVariables(direction)
        val step = source.replaceVariables(steps)
        val resultName = source.replaceVariables(resultName)

        throwIfInvalidNumber(step)

        val direction = entity.direction.horizontalIndex
        val entityPos = entity.position.floor().add(0.5, 0.5, 0.5)
        val pos = when (side) {
            SIDE_DOWN, SIDE_UP, SIDE_NORTH, SIDE_SOUTH, SIDE_WEST, SIDE_EAST ->
                entityPos.getSide(vector3SideMap[side], step.toInt())
            SIDE_LEFT -> entityPos.getSide(directionSideMap[(direction + 3) % 4], step.toInt())
            SIDE_BEHIND -> entityPos.getSide(directionSideMap[(direction + 2) % 4], step.toInt())
            SIDE_RIGHT -> entityPos.getSide(directionSideMap[(direction + 1) % 4], step.toInt())
            SIDE_FRONT -> entityPos.getSide(directionSideMap[(direction) % 4], step.toInt())
            else -> throw InvalidFlowValueException(
                Language.get(
                    "action.getEntitySide.direction.notFound",
                    listOf(side)
                )
            )
        }

        source.addVariable(resultName, PositionObjectVariable(Position.fromObject(pos, entity.level)))
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            EntityVariableDropdown(variables, getEntityVariableName()),
            Dropdown("@action.getEntitySide.form.direction", directions, directions.indexOf(direction)),
            ExampleNumberInput("@action.getEntitySide.form.steps", "1", steps, true),
            ExampleInput("@action.form.resultVariableName", "pos", resultName, true),
        )
    }

    override fun parseFromFormData(data: CustomFormResponseList): List<Any?> {
        return listOf(data[0], directions.getOrElse(data.getInt(1)) { "" }, data[2], data[3])
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setEntityVariableName(contents.getString(0))
        direction = contents.getString(1)
        steps = contents.getString(2)
        resultName = contents.getString(3)
    }

    override fun serializeContents(): List<Any> {
        return listOf(getEntityVariableName(), direction, steps, resultName)
    }

    override fun getAddingVariables(): DummyVariableMap {
        return mapOf(
            resultName to DummyVariable(DummyVariable.Type.POSITION)
        )
    }

    override fun clone(): GetEntitySidePosition {
        val item = super.clone() as GetEntitySidePosition
        item.entityVariableNames = entityVariableNames.toMutableMap()
        return item
    }
}