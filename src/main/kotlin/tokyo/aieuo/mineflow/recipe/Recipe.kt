package tokyo.aieuo.mineflow.recipe

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.entity.Entity
import cn.nukkit.event.Event
import io.github.g00fy2.versioncompare.Version
import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.exception.FlowItemLoadException
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemContainer
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.trigger.Trigger
import tokyo.aieuo.mineflow.trigger.TriggerHolder
import tokyo.aieuo.mineflow.trigger.Triggers
import tokyo.aieuo.mineflow.utils.JsonSerializable
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.utils.Logger
import tokyo.aieuo.mineflow.utils.json_encode
import tokyo.aieuo.mineflow.variable.DefaultVariables
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.Variable
import tokyo.aieuo.mineflow.variable.obj.EventObjectVariable
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class Recipe(var name: String, group: String = "", var author: String = "", var version: String? = null): FlowItemContainer, JsonSerializable, Cloneable {

    var group: String = group
        set (value) {
            field = value.replace(Regex("""/+"""), "/")
        }

    var targetType: Int = TARGET_DEFAULT
    val targetOptions: MutableMap<String, Any> = mutableMapOf()

    val triggers: MutableList<Trigger> = mutableListOf()

    var arguments: List<String> = listOf()
    var returnValues: List<String> = listOf()

    var executor: FlowItemExecutor? = null

    internal var rawData = ""

    override var items: MutableMap<String, MutableList<FlowItem>> = mutableMapOf()

    override fun getContainerName(): String {
        return name
    }

    fun getPathname(): String {
        return if (group.isEmpty()) name else ("$group/$name")
    }

    fun getDetail(): String {
        val details = mutableListOf<String>()
        for (trigger in triggers) {
            details.add(trigger.toString())
        }
        details.add("~".repeat(20))
        for (action in getActions()) {
            details.add(action.getDetail())
        }
        return details.joinToString("\n§f")
    }

    fun setTargetSetting(type: Int, options: Map<String, Any> = mapOf()) {
        targetType = type
        options.forEach { (k, v) -> targetOptions[k] = v }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getTargetOption(key: String, default: T): T {
        return targetOptions[key]?.let { it as T } ?: default
    }

    @Suppress("UNCHECKED_CAST")
    fun getTargets(player: Entity? = null): List<Entity?> {
        return when (targetType) {
            TARGET_NONE -> listOf(null)
            TARGET_DEFAULT -> listOf(player)
            TARGET_SPECIFIED -> {
                val targets = mutableListOf<Entity?>()
                for (targetName in targetOptions["specified"] as? List<String> ?: listOf()) {
                    val target = Server.getInstance().getPlayer(targetName)
                    if (target !is Player) continue
                    targets.add(target)
                }
                targets
            }
            TARGET_ON_WORLD -> if (player === null) listOf() else  player.level.players.values.toList()
            TARGET_BROADCAST -> Server.getInstance().onlinePlayers.values.toList()
            TARGET_RANDOM -> {
                val targets = mutableListOf<Entity?>()
                val onlines = Server.getInstance().onlinePlayers
                val max = (targetOptions["random"] as? Int ?: 1).let { if (it > onlines.size) onlines.size else it }
                for ((c, p) in onlines.values.shuffled().withIndex()) {
                    targets.add(p)
                    if (c >= max) break
                }
                targets
            }
            else -> listOf()
        }
    }

    fun addTrigger(trigger: Trigger) {
        TriggerHolder.addRecipe(trigger, this)
        triggers.add(trigger)
    }

    fun setTriggersFromArray(triggers: List<Map<String, String>>) {
        removeTriggerAll()
        for (triggerData in triggers) {
            val type = triggerData["type"] ?: continue
            val key = triggerData["key"] ?: continue
            val subKey = triggerData["subKey"]
            val trigger = Triggers.getTrigger(type, key, subKey ?: "")
            if (trigger === null) throw Exception(Language.get("trigger.notFound", listOf(type)))
            addTrigger(trigger)
        }
    }

    fun existsTrigger(trigger: Trigger): Boolean {
        return trigger in triggers
    }

    fun removeTrigger(trigger: Trigger) {
        TriggerHolder.removeRecipe(trigger, this)
        triggers.remove(trigger)
    }

    fun removeTriggerAll() {
        for (trigger in triggers) {
            TriggerHolder.removeRecipe(trigger, this)
        }
        triggers.clear()
    }

    fun executeAllTargets(player: Entity? = null, _variables: Map<String, Variable<Any>> = mapOf(), event: Event? = null, args: List<Variable<Any>> = listOf(), callbackExecutor: FlowItemExecutor? = null): Boolean {
        val targets = getTargets(player)
        val variables = _variables + DefaultVariables.getServerVariables()

        for (target in targets) {
            val recipe = clone()
            recipe.execute(target, event, variables, args, callbackExecutor)
        }
        return true
    }

    fun execute(target: Entity?, event: Event? = null, _variables: Map<String, Variable<Any>> = mapOf(), args: List<Variable<Any>> = listOf(), callbackExecutor: FlowItemExecutor? = null): Boolean {
        val variables = _variables.toMutableMap()
        for ((i, argument) in arguments.withIndex()) {
            if (args.size < i) continue

            val arg = args[i]
            variables[argument] = arg
        }
        if (target !== null) {
            variables.putAll(DefaultVariables.getEntityVariables(target))
        }
        if (event !== null) {
            variables["event"] = EventObjectVariable(event)
        }

        executor = FlowItemExecutor(getActions(), target, variables, null, event, { executor ->
            if (callbackExecutor !== null) {
                for (value in returnValues) {
                    val variable = executor.getVariable(value)
                    if (variable is Variable) callbackExecutor.addVariable(value, variable)
                }
                callbackExecutor.resume()
            }
        }, { item, _ ->
            Logger.warning(Language.get("recipe.execute.failed", listOf(getPathname(), item.getName())), target)
        }, this)
        executor!!.execute()
        return true
    }

    override fun getAddingVariablesBefore(flowItem: FlowItem, containers: MutableList<FlowItemContainer>, type: String): Map<String, DummyVariable<DummyVariable.Type>> {
        val variables = mutableMapOf(
            "target" to DummyVariable(DummyVariable.Type.PLAYER)
        )

        for (trigger in triggers) {
            variables.putAll(trigger.getVariablesDummy())
        }
        variables.putAll(super.getAddingVariablesBefore(flowItem, containers, type))
        return variables
    }

    fun loadSaveData(contents: List<Map<String, Any>>) = apply {
        for ((i, content) in contents.withIndex()) {
            val action = try {
                FlowItem.loadEachSaveData(content)
            } catch (e: Exception) {
                when (e) {
                    is IndexOutOfBoundsException -> throw FlowItemLoadException(
                        Language.get("recipe.load.failed.action", listOf(
                            i.toString(),
                            content.getOrDefault("id", "id?") as String,
                            Language.get("recipe.json.key.missing")
                        ))
                    )
                    else -> throw e
                }
            }

            addItem(action, FlowItemContainer.ACTION)
        }
    }

    override fun jsonSerialize(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "group" to group,
            "plugin_version" to version,
            "author" to author,
            "actions" to getActions(),
            "triggers" to triggers,
            "target" to mapOf(
                "type" to targetType,
                "options" to targetOptions,
            ),
            "arguments" to arguments,
            "returnValues" to returnValues,
        )
    }

    fun getFileName(_baseDir: String): String {
        var baseDir = _baseDir
        val group = group.replace(Regex(""""[.¥:?<>|*"]"""), "")
        val name = name.replace(Regex(""""[.¥:?<>|*"]"""), "")
        if (group.isNotEmpty()) baseDir += "$group/"
        return "$baseDir$name.json"
    }

    fun save(dir: String) {
        val path = getFileName(dir)

        path.substring(0, path.lastIndexOf("/")).let {
            if (!Files.exists(Path.of(it))) File(it).mkdirs()
        }

        val json = json_encode(this)
        if (json == rawData) return

        try {
            val fw = FileWriter(File(path))
            fw.write(json)
            fw.close()
        } catch (e: IOException) {
            Main.instance.logger.error(Language.get("recipe.save.failed", listOf(getPathname())))
        }
    }

    fun checkVersion() {
        val createdVersion = version?.let { Version(it) }
        val currentVersion = Version(Main.pluginVersion)

        if (createdVersion !== null && createdVersion == currentVersion) return

        upgrade(createdVersion, currentVersion)
    }

    fun upgrade(_from: Version?, to: Version) {
        var from = _from

        if (Version("2.0.0") <= to && (from === null || from < Version("2.0.0"))) {
            val oldToNewTargetMap = mapOf(
                4 to TARGET_NONE,
                0 to TARGET_DEFAULT,
                1 to TARGET_SPECIFIED,
                2 to TARGET_BROADCAST,
                3 to TARGET_RANDOM,
            )
            oldToNewTargetMap[targetType]?.let {
                targetType = it
            }
            for (action in flattenFlowItems(this, FlowItemContainer.ACTION)) {
                replaceLevelToWorld(action)
            }
            for (condition in flattenFlowItems(this, FlowItemContainer.CONDITION)) {
                replaceLevelToWorld(condition)
            }

            from = Version("2.0.0")
        }

        version = from?.originalString
    }

    private fun replaceLevelToWorld(action: FlowItem) {
        val newContents = mutableListOf<Any>()
        for (data in action.serializeContents()) {
            newContents.add(if (data is String) {
                data.replace("origin_level", "origin_world")
                    .replace("target_level", "target_world")
                    .replace(Regex("""(\{.+.)level((.?.+)*})"""), "$1world$2")
            } else {
                data
            })
        }
        action.loadSaveData(CustomFormResponseList(newContents))
    }

    fun flattenFlowItems(container: FlowItemContainer, type: String): List<FlowItem> {
        val flat = mutableListOf<FlowItem>()
        for (item in container.getItems(type)) {
            if (item is FlowItemContainer) {
                flat.addAll(flattenFlowItems(item, type))
            } else {
                flat.add(item)
            }
        }
        return flat
    }

    public override fun clone(): Recipe {
        val recipe = super.clone() as Recipe

        recipe.items = mutableMapOf()
        val actions = mutableListOf<FlowItem>()
        for (action in getActions()) {
            actions.add(action.clone())
        }
        recipe.setItems(actions, FlowItemContainer.ACTION)
        return recipe
    }

    companion object {
        const val TARGET_NONE = 0
        const val TARGET_DEFAULT = 1
        const val TARGET_SPECIFIED = 2
        const val TARGET_ON_WORLD = 3
        const val TARGET_BROADCAST = 4
        const val TARGET_RANDOM = 5
    }
}