package tokyo.aieuo.mineflow.flowItem.action.script

import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.exception.InvalidFlowValueException
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.recipe.Recipe
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.StringVariable
import tokyo.aieuo.mineflow.variable.Variable

open class ExecuteRecipe(var recipeName: String = "", var args: List<String> = listOf()): FlowItem() {

    override val id = FlowItemIds.EXECUTE_RECIPE

    override val nameTranslationKey = "action.executeRecipe.name"
    override val detailTranslationKey = "action.executeRecipe.detail"
    override val detailDefaultReplaces = listOf("name")

    override val category = Category.SCRIPT

    override val permission = PERMISSION_LEVEL_1

    override fun isDataValid(): Boolean {
        return recipeName != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(recipeName))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val recipe = getRecipe(source).clone()
        val args = getArguments(source)

        recipe.executeAllTargets(source.target, source.getVariables(), source.event, args)
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    fun getRecipe(source: FlowItemExecutor): Recipe {
        val name = source.replaceVariables(recipeName)

        val recipeManager = Main.recipeManager
        var (recipeName, group) = recipeManager.parseName(name)
        if (group.isEmpty()) {
            val sr = source.sourceRecipe
            if (sr !== null) group = sr.group
        }

        val recipe = recipeManager.get(recipeName, group) ?: recipeManager.get(recipeName, "")
        if (recipe === null) {
            throw InvalidFlowValueException(Language.get("action.executeRecipe.notFound"))
        }

        return recipe
    }

    fun getArguments(source: FlowItemExecutor): List<Variable<Any>> {
        val helper = Main.variableHelper
        val args = mutableListOf<Variable<Any>>()
        for (arg in this.args) {
            if (!helper.isVariableString(arg)) {
                args.add(StringVariable(helper.replaceVariables(arg, source.getVariables())))
                continue
            }

            arg.substring(1, arg.length - 1).let {
                args.add(source.getVariable(it) ?: helper.get(it) ?: StringVariable(arg))
            }
        }
        return args
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            ExampleInput("@action.executeRecipe.form.name", "aieuo", recipeName, true),
            ExampleInput("@action.callRecipe.form.args", "{target}, 1, aieuo", args.joinToString(", "), false),
        )
    }

    override fun parseFromFormData(data: CustomFormResponseList): List<Any?> {
        return listOf(data[0], (data.getString(1)).split(",").map { it.trim() })
    }

    @Suppress("UNCHECKED_CAST")
    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        recipeName = contents.getString(0)
        args = contents[1] as List<String>
    }

    override fun serializeContents(): List<Any> {
        return listOf(recipeName, args)
    }
}