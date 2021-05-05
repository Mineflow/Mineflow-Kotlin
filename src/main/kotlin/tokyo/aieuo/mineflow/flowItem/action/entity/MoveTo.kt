package tokyo.aieuo.mineflow.flowItem.action.entity

import cn.nukkit.math.Vector3
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.EntityFlowItem
import tokyo.aieuo.mineflow.flowItem.base.PositionFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.EntityVariableDropdown
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleNumberInput
import tokyo.aieuo.mineflow.formAPI.element.mineflow.PositionVariableDropdown
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable

class MoveTo(var entity: String = "", var position: String = "", var x: String = "0.1", var y: String = "0", var z: String = "0.1")
    : FlowItem(), EntityFlowItem, PositionFlowItem {

    override val id = FlowItemIds.MOVE_TO

    override val nameTranslationKey = "action.moveTo.name"
    override val detailTranslationKey = "action.moveTo.detail"
    override val detailDefaultReplaces = listOf("entity", "position", "speedX", "speedY", "speedZ")

    override val category = Category.ENTITY
    override val permission = PERMISSION_LEVEL_1

    override var entityVariableNames: MutableMap<String, String> = mutableMapOf()
    override var positionVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setEntityVariableName(entity)
        setPositionVariableName(position)
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getEntityVariableName(), getPositionVariableName(), x, y, z))
    }

    override fun isDataValid(): Boolean {
        return getEntityVariableName() != "" && getPositionVariableName() != "" && x != "" && y != "" && z != ""
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val entity = getEntity(source)
        throwIfInvalidEntity(entity)

        val position = getPosition(source)

        val speedX = source.replaceVariables(x)
        throwIfInvalidNumber(speedX, 0.0)

        val speedY = source.replaceVariables(y)
        throwIfInvalidNumber(speedY, 0.0)

        val speedZ = source.replaceVariables(z)
        throwIfInvalidNumber(speedZ, 0.0)

        val dis = entity.distance(position)
        if (dis > 1) {
            val x = speedX.toFloat() * ((position.x - entity.x) / dis)
            val y = speedY.toFloat() * ((position.y - entity.y) / dis)
            val z = speedZ.toFloat() * ((position.z - entity.z) / dis)

            entity.motion = Vector3(x, y, z)
        }
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            EntityVariableDropdown(variables, getEntityVariableName()),
            PositionVariableDropdown(variables, getPositionVariableName()),
            ExampleNumberInput("@action.moveTo.form.speedX", "0.1", x),
            ExampleNumberInput("@action.moveTo.form.speedY", "0", y),
            ExampleNumberInput("@action.moveTo.form.speedZ", "0.1", z),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setEntityVariableName(contents.getString(0))
        setPositionVariableName(contents.getString(1))
        x = contents.getString(2)
        y = contents.getString(3)
        z = contents.getString(4)
    }

    override fun serializeContents(): List<Any> {
        return listOf(getEntityVariableName(), getPositionVariableName(), x, y, z)
    }

    override fun clone(): MoveTo {
        val item = super.clone() as MoveTo
        item.entityVariableNames = entityVariableNames.toMutableMap()
        item.positionVariableNames = positionVariableNames.toMutableMap()
        return item
    }
}
