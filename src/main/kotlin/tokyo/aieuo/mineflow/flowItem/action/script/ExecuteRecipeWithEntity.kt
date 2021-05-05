package tokyo.aieuo.mineflow.flowItem.action.script

import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.EntityFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.EntityVariableDropdown
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable

class ExecuteRecipeWithEntity(name: String = "", entity: String = ""): ExecuteRecipe(name), EntityFlowItem {

    override val id = FlowItemIds.EXECUTE_RECIPE_WITH_ENTITY

    override val nameTranslationKey = "action.executeRecipeWithEntity.name"
    override val detailTranslationKey = "action.executeRecipeWithEntity.detail"
    override val detailDefaultReplaces = listOf("name", "target")

    override var entityVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setEntityVariableName(entity)
    }

    override fun isDataValid(): Boolean {
        return recipeName != "" && getEntityVariableName() != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(recipeName, getEntityVariableName()))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val recipe = getRecipe(source).clone()

        val entity = getEntity(source)
        throwIfInvalidEntity(entity)

        recipe.execute(entity, source.event, source.getVariables())
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            ExampleInput("@action.executeRecipe.form.name", "aieuo", recipeName, true),
            EntityVariableDropdown(variables, getEntityVariableName()),
        )
    }

    override fun parseFromFormData(data: CustomFormResponseList): List<Any?> {
        return data
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        recipeName = contents.getString(0)
        setEntityVariableName(contents.getString(1))
    }

    override fun serializeContents(): List<Any> {
        return listOf(recipeName, getEntityVariableName())
    }

    override fun clone(): ExecuteRecipeWithEntity {
        val item = super.clone() as ExecuteRecipeWithEntity
        item.entityVariableNames = entityVariableNames.toMutableMap()
        return item
    }
}