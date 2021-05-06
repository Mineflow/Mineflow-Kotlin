package tokyo.aieuo.mineflow.flowItem.action.entity

import cn.nukkit.math.Vector3
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.EntityFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.EntityVariableDropdown
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleNumberInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language

class Motion(var entity: String = "", var x: String = "0", var y: String = "0", var z: String = "0") : FlowItem(),
    EntityFlowItem {

    override val id = FlowItemIds.MOTION

    override val nameTranslationKey = "action.motion.name"
    override val detailTranslationKey = "action.motion.detail"
    override val detailDefaultReplaces = listOf("entity", "x", "y", "z")

    override val category = Category.ENTITY

    override var entityVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setEntityVariableName(entity)
    }

    override fun isDataValid(): Boolean {
        return getEntityVariableName() != "" && x != "" && y != "" && z != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getEntityVariableName(), x, y, z))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val x = source.replaceVariables(x)
        throwIfInvalidNumber(x)

        val y = source.replaceVariables(y)
        throwIfInvalidNumber(y)

        val z = source.replaceVariables(z)
        throwIfInvalidNumber(z)

        val entity = getEntity(source)
        throwIfInvalidEntity(entity)

        val motion = Vector3(x.toDouble(), y.toDouble(), z.toDouble())
        entity.motion = motion
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            EntityVariableDropdown(variables, getEntityVariableName()),
            ExampleNumberInput("@action.motion.form.x", "2", x, true),
            ExampleNumberInput("@action.motion.form.y", "3", y, true),
            ExampleNumberInput("@action.motion.form.z", "4", z, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setEntityVariableName(contents.getString(0))
        x = contents.getString(1)
        y = contents.getString(2)
        z = contents.getString(3)
    }

    override fun serializeContents(): List<Any> {
        return listOf(getEntityVariableName(), x, y, z)
    }

    override fun clone(): Motion {
        val item = super.clone() as Motion
        item.entityVariableNames = entityVariableNames.toMutableMap()
        return item
    }
}