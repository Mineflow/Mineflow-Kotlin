package tokyo.aieuo.mineflow.recipe

import cn.nukkit.entity.Entity
import cn.nukkit.event.Event
import tokyo.aieuo.mineflow.variable.Variable

class RecipeContainer(val recipes: MutableMap<String, Recipe> = mutableMapOf()) {

    var changed = false

    fun addRecipe(recipe: Recipe) {
        recipes[recipe.getPathname()] = recipe
        changed = true
    }

    fun getRecipe(key: String): Recipe? {
        return recipes[key]
    }

    fun getAllRecipe(): MutableMap<String, Recipe> {
        return recipes
    }

    fun removeRecipe(key: String) {
        recipes.remove(key)
    }

    fun existsRecipe(key: String): Boolean {
        return recipes.containsKey(key)
    }

    fun getRecipeCount(): Int {
        return getAllRecipe().size
    }

    fun executeAll(target: Entity? = null, variables: Map<String, Variable<Any>> = mapOf(), event: Event? = null) {
        for ((_, recipe) in getAllRecipe()) {
            recipe.executeAllTargets(target, variables, event)
        }
    }
}