package tokyo.aieuo.mineflow.flowItem

import cn.nukkit.entity.Entity
import cn.nukkit.event.Event
import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.exception.*
import tokyo.aieuo.mineflow.recipe.Recipe
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.utils.Logger
import tokyo.aieuo.mineflow.utils.VariableMap
import tokyo.aieuo.mineflow.variable.Variable

class FlowItemExecutor(
    val items: List<FlowItem>,
    val target: Entity? = null,
    val localVariables: MutableMap<String, Variable<Any>> = mutableMapOf(),
    val parent: FlowItemExecutor? = null,
    var event: Event? = null,
    val onComplete: ((FlowItemExecutor) -> Unit)? = null,
    val onError: ((FlowItem, Entity?) -> Unit)? = null,
    sourceRecipe: Recipe? = null
) {

    val sourceRecipe: Recipe? = sourceRecipe
        get() = parent?.sourceRecipe ?: field

    var lastResult: Result = Result.CONTINUE
    var currentFlowItem: FlowItem? = null

    var generator: Iterator<Result>? = null

    var waiting = false
    var exit = false
    var resuming = false

    init {
        if (event === null && parent !== null) {
            event = parent.event
        }
    }

    fun executeGenerator(): Iterator<Result> {
        val source = this
        return iterator {
            items.forEach { item ->
                currentFlowItem = item
                item.execute(source).forEach {
                    lastResult = it
                    yield(it)
                }
            }
        }
    }

    fun execute(): Boolean {
        val generator = generator ?: executeGenerator()
        if (this.generator === null) this.generator = generator

        try {
            while (generator.hasNext()) {
                if (exit) {
                    resuming = false
                    waiting = false
                    return false
                }

                val result = generator.next()
                if (result == Result.AWAIT && !resuming) {
                    waiting = true
                    return false
                }

                if (result == Result.CONTINUE) {
                    resuming = false
                }
            }
        } catch (e: Exception) {
            when (e) {
                is InvalidFlowValueException -> currentFlowItem?.let {
                    val message = e.message
                    if (message?.isNotEmpty() == true) Logger.warning(
                        Language.get("action.error", listOf(it.getName(), message)),
                        target
                    )
                    if (onError !== null) (onError)(it, target)
                }
                is UndefinedMineflowVariableException,
                is UndefinedMineflowPropertyException,
                is UndefinedMineflowMethodException,
                is UnsupportedCalculationException -> currentFlowItem?.let {
                    val message = e.message
                    if (message?.isNotEmpty() == true) Logger.warning(message, target)
                    if (onError !== null) (onError)(it, target)
                }
                else -> throw e
            }
        }

        if (onComplete !== null) (onComplete)(this)
        return true
    }

    fun resume() {
        if (parent !== null) parent.resume()

        resuming = true
        if (!waiting) return

        resuming = false
        waiting = false
        execute()
    }

    fun exit() {
        if (parent !== null) parent.exit()

        exit = true
    }

    fun replaceVariables(text: String): String {
        return Main.variableHelper.replaceVariables(text, getVariables())
    }

    fun getVariable(_name: String): Variable<Any>? {
        val names = ArrayDeque(_name.split("."))
        val name = names.firstOrNull() ?: _name
        names.removeFirst()

        var variable = localVariables[name] ?: (if (parent === null) null else parent.getVariable(name))

        if (variable === null) return null

        for (name1 in names) {
            if (variable !is Variable) return null
            variable = variable.getValueFromIndex(name1)
        }
        return variable
    }

    fun getVariables(): VariableMap {
        val variables = localVariables.toMutableMap()
        if (parent !== null) {
            variables.putAll(parent.getVariables())
        }
        return variables
    }

    fun addVariable(name: String, variable: Variable<Any>, onlyThisScope: Boolean = false) {
        localVariables[name] = variable

        if (!onlyThisScope && parent !== null) {
            parent.addVariable(name, variable)
        }
    }

    fun removeVariable(name: String) {
        localVariables.remove(name)
        if (parent !== null) parent.removeVariable(name)
    }

    enum class Result {
        CONTINUE,
        AWAIT,
        SUCCESS,
        FAILURE;

        fun fromBoolean(result: Boolean): Result {
            return if (result) SUCCESS else FAILURE
        }
    }
}