package tokyo.aieuo.mineflow.flowItem.action.entity

import cn.nukkit.Player
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.EntityFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.EntityVariableDropdown
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable

class SetNameTag(var entity: String = "", var newName: String = ""): FlowItem(), EntityFlowItem {

    override val id = FlowItemIds.SET_NAME

    override val nameTranslationKey = "action.setNameTag.name"
    override val detailTranslationKey = "action.setNameTag.detail"
    override val detailDefaultReplaces = listOf("entity", "name")

    override val category = Category.ENTITY

    override var entityVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setEntityVariableName(entity)
    }

    override fun isDataValid(): Boolean {
        return getEntityVariableName() != "" && newName != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getEntityVariableName(), newName))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val name = source.replaceVariables(newName)

        val entity = getEntity(source)
        throwIfInvalidEntity(entity)

        entity.nameTag = name
        if (entity is Player) entity.displayName = name
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            EntityVariableDropdown(variables, getEntityVariableName()),
            ExampleInput("@action.setNameTag.form.name", "aieuo", newName, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setEntityVariableName(contents.getString(0))
        newName = contents.getString(1)
    }

    override fun serializeContents(): List<Any> {
        return listOf(getEntityVariableName(), newName)
    }

    override fun clone(): SetNameTag {
        val item = super.clone() as SetNameTag
        item.entityVariableNames = entityVariableNames.toMutableMap()
        return item
    }
}