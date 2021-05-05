package tokyo.aieuo.mineflow.trigger

import tokyo.aieuo.mineflow.recipe.Recipe
import tokyo.aieuo.mineflow.recipe.RecipeContainer

object TriggerHolder {

    private val all: MutableMap<String, MutableMap<String, MutableMap<String, RecipeContainer>>> = mutableMapOf()

    fun createContainer(trigger: Trigger) {
        all.getOrPut(trigger.type) { mutableMapOf() }
            .getOrPut(trigger.key) { mutableMapOf() }
            .getOrPut(trigger.subKey) { RecipeContainer() }
    }

    fun existsRecipe(trigger: Trigger): Boolean {
        return getRecipes(trigger) !== null
    }

    fun existsRecipe(type: String, key: String, subKey: String = ""): Boolean {
        return getRecipes(type, key, subKey) !== null
    }

    fun addRecipe(trigger: Trigger, recipe: Recipe) {
        createContainer(trigger)
        getRecipes(trigger)?.addRecipe(recipe)
    }

    fun removeRecipe(trigger: Trigger, recipe: Recipe) {
        val container = getRecipes(trigger) ?: return

        container.removeRecipe(recipe.getPathname())
        if (container.getRecipeCount() == 0) {
            all[trigger.type]?.get(trigger.key)?.remove(trigger.subKey)
        }
    }

    fun getRecipes(trigger: Trigger): RecipeContainer? {
        return getRecipes(trigger.type, trigger.key, trigger.subKey)
    }

    fun getRecipes(type: String, key: String, subKey: String = ""): RecipeContainer? {
        return all[type]?.get(key)?.get(subKey)
    }

    fun getRecipesWithSubKey(trigger: Trigger): MutableMap<String, RecipeContainer> {
        return all[trigger.type]?.get(trigger.key)?.toMutableMap() ?: mutableMapOf()
    }
}