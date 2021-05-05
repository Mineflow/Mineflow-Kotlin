package tokyo.aieuo.mineflow.ui

import cn.nukkit.Player
import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.formAPI.CustomForm
import tokyo.aieuo.mineflow.formAPI.FormError
import tokyo.aieuo.mineflow.formAPI.ListForm
import tokyo.aieuo.mineflow.formAPI.element.Button
import tokyo.aieuo.mineflow.formAPI.element.CancelToggle
import tokyo.aieuo.mineflow.formAPI.element.Input
import tokyo.aieuo.mineflow.formAPI.element.Toggle
import tokyo.aieuo.mineflow.recipe.Recipe
import tokyo.aieuo.mineflow.recipe.RecipePack
import tokyo.aieuo.mineflow.utils.Language

object ExportForm {

    fun sendRecipeListByRecipe(player: Player, recipe: Recipe) {
        val recipes = Main.recipeManager.getWithLinkedRecipes(recipe, recipe)
        sendRecipeList(player, recipes.values.toMutableList())
    }

    fun sendRecipeList(player: Player, recipes: MutableList<Recipe>, messages: List<String> = listOf()) {
        val buttons = mutableListOf(
            Button("@form.export.execution") { sendExportMenu(player, recipes) },
            Button("@form.add") {
                MineflowForm.selectRecipe(player, "@form.export.selectRecipe.title", { recipe ->
                    sendRecipeList(player, (recipes + Main.recipeManager.getWithLinkedRecipes(recipe, recipe).values).toMutableList(), listOf("@form.added"))
                }, {
                    sendRecipeList(player, recipes, listOf("@form.cancelled"))
                })
            }
        )
        for ((i, recipe) in recipes.withIndex()) {
            buttons.add(Button("${recipe.group}/${recipe.name}") {
                sendRecipeMenu(player, recipes, i)
            })
        }

        (ListForm("@form.export.recipeList.title"))
            .setButtons(buttons)
            .addMessages(messages)
            .show(player)
    }

    fun sendRecipeMenu(player: Player, recipes: MutableList<Recipe>, index: Int) {
        val recipe = recipes[index]
        (ListForm(recipe.name))
            .setButtons(mutableListOf(
                Button("@form.back") { sendRecipeList(player, recipes) },
                Button("@form.delete") { sendRecipeList(player, recipes.also { it.removeAt(index) }, listOf("@form.deleted")) },
            )).show(player)
    }

    fun sendExportMenu(player: Player, recipes: MutableList<Recipe>) {
        if (recipes.isEmpty()) {
            sendRecipeList(player, recipes, listOf("@form.export.empty"))
            return
        }

        (CustomForm("@mineflow.export"))
            .setContents(mutableListOf(
                Input("@form.export.name", required = true),
                Input("@form.export.author", default = player.name, required = true),
                Input("@form.export.detail"),
                Toggle("@form.export.includeConfig", true),
                CancelToggle { sendRecipeList(player, recipes, listOf("@form.cancelled")) },
            )).onReceive { data ->
                val name = data.getString(0)
                val author = data.getString(1)
                val detail = data.getString(2)

                val errors = mutableListOf<FormError>()
                if (name.contains(Regex("[.¥/:?<>|*\"]"))) errors.add("@form.recipe.invalidName" to 0)

                if (errors.isNotEmpty()) {
                    resend(errors)
                    return@onReceive
                }

                val pack = if (data.getBoolean(3)) {
                    RecipePack(name, author, detail, recipes)
                } else {
                    RecipePack(name, author, detail, recipes, configs = mutableMapOf())
                }
                pack.export("${Main.instance.dataFolder.path}/exports/")

                player.sendMessage(Language.get("form.export.success", listOf("${name}.json")))
            }.show(player)
    }

}