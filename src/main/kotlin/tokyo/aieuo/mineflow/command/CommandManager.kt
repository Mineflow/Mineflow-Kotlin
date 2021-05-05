package tokyo.aieuo.mineflow.command

import cn.nukkit.command.PluginCommand
import cn.nukkit.utils.Config
import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.trigger.TriggerHolder
import tokyo.aieuo.mineflow.trigger.command.CommandTrigger

class CommandManager(private val owner: Main, private val config: Config) {

    val commands: MutableMap<String, PluginCommand<Main>> = mutableMapOf()
    val commandData: MutableMap<String, CustomCommandData> = mutableMapOf()

    init {
        loadCommands()
        registerCommands(commandData.values.toList())
    }

    fun loadCommands() {
        commandData.clear()
        val commands = config.all

        for ((_, v) in commands) {
            val data = v as? Map<*, *> ?: continue
            val command = data["command"] as? String ?: continue
            val permission = data["permission"] as? String ?: "mineflow.customcommand.op"
            val description = data["description"] as? String ?: ""

            commandData[command] = CustomCommandData(command, permission, description)
        }
    }

    fun isRegistered(command: String): Boolean {
        return owner.server.getPluginCommand(command) !== null
    }

    private fun registerCommands(commands: List<CustomCommandData>) {
        for (command in commands) {
            registerCommand(command)
        }
    }

    fun registerCommand(commandData: CustomCommandData): Boolean {
        return registerCommand(commandData.command, commandData.permission, commandData.description)
    }

    fun registerCommand(_commandStr: String, permission: String, description: String = ""): Boolean {
        var commandStr = _commandStr
        if (isSubcommand(commandStr)) commandStr = getOriginCommand(commandStr)

        if (isRegistered(commandStr)) return false

        val command = PluginCommand(commandStr, owner)
        command.description = description
        command.permission = permission
        commands[commandStr] = command
        owner.server.commandMap.register("mineflow", command)
        return true
    }

    fun unregisterCommand(commandName: String) {
        if (!commands.containsKey(commandName)) return

        owner.server.commandMap.let { commandMap ->
            val command = commandMap.getCommand(commandName) ?: return@let
            command.unregister(commandMap)
        }

        commands.remove(commandName)
    }

    fun existsCommand(commandStr: String): Boolean {
        return config.exists(commandStr)
    }

    fun addCommand(command: CustomCommandData) {
        config.set(command.command, command.toMap())
        config.save()

        commandData[command.command] = command
        if (!isRegistered(command.command)) registerCommand(command)
    }

    fun addCommand(commandStr: String, permission: String, description: String = "") {
        val origin = getOriginCommand(commandStr)
        val subCommands = getSubcommandsFromCommand(commandStr)

        val command = CustomCommandData(origin, permission, description, subCommands)
        addCommand(command)
    }

    fun getCommand(commandStr: String): CustomCommandData? {
        return commandData[commandStr]
    }

    fun removeCommand(commandStr: String) {
        unregisterCommand(getOriginCommand(commandStr))
        config.remove(commandStr)
        config.save()
        commandData.remove(commandStr)
    }

    fun updateCommand(command: CustomCommandData) {
        if (!existsCommand(command.command)) {
            addCommand(command)
            return
        }

        unregisterCommand(command.command)
        registerCommand(command)

        config.set(command.command, command.toMap())
        config.save()

        commandData[command.command] = command
    }

    fun getAssignedRecipes(_command: String): Map<String, List<String>> {
        val command = getOriginCommand(_command)
        val recipes: MutableMap<String, MutableList<String>> = mutableMapOf()
        val containers = TriggerHolder.getRecipesWithSubKey(CommandTrigger.create(command))

        for ((name, container) in containers) {
            for ((_, recipe) in container.getAllRecipe()) {
                val path = "${recipe.group}/${recipe.name}"

                if (!recipes.containsKey(path)) recipes[path] = mutableListOf()
                recipes[path]?.add(name)
            }
        }
        return recipes
    }

    fun isSubcommand(command: String): Boolean {
        return command.contains(" ")
    }

    fun getSubcommandsFromCommand(command: String): Map<String, Any> {
        if (!isSubcommand(command)) return mapOf()

        val subCommands = mutableMapOf<String, Any>()
        val commands = command.split(" ").toMutableList()
        commands.removeAt(0)
        val command1 = commands.joinToString(" ")
        subCommands[getOriginCommand(command1)] = getSubcommandsFromCommand(command1)
        return subCommands
    }

    fun getOriginCommand(command: String): String {
        val commands = command.split(" ")
        return commands[0]
    }
}