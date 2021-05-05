package tokyo.aieuo.mineflow.utils

object Category {

    const val COMMON = "common"
    const val BLOCK = "block"
    const val COMMAND = "command"
    const val ENTITY = "entity"
    const val EVENT = "event"
    const val FORM = "form"
    const val INVENTORY = "inventory"
    const val ITEM = "item"
    const val WORLD = "world"
    const val PLAYER = "player"
    const val PLUGIN = "plugin"
    const val MATH = "math"
    const val STRING = "string"
    const val VARIABLE = "variable"
    const val SCRIPT = "script"
    const val SCOREBOARD = "scoreboard"

    val categories = mutableListOf(
        COMMON,
        PLAYER,
        ENTITY,
        INVENTORY,
        ITEM,
        COMMAND,
        BLOCK,
        WORLD,
        EVENT,
        SCRIPT,
        MATH,
        VARIABLE,
        STRING,
        FORM,
        SCOREBOARD,
        PLUGIN,
    )

    fun existsCategory(category: String): Boolean {
        return category in categories
    }

    fun addCategory(category: String): Boolean {
        if (existsCategory(category)) return false

        categories.add(category)
        return true
    }
}