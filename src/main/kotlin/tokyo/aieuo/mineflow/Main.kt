package tokyo.aieuo.mineflow

import cn.nukkit.plugin.PluginBase
import cn.nukkit.utils.Config
import tokyo.aieuo.mineflow.command.CommandManager
import tokyo.aieuo.mineflow.command.MineflowCommand
import tokyo.aieuo.mineflow.economy.Economy
import tokyo.aieuo.mineflow.entity.EntityManager
import tokyo.aieuo.mineflow.event.ServerStartEvent
import tokyo.aieuo.mineflow.flowItem.FlowItemFactory
import tokyo.aieuo.mineflow.recipe.RecipeManager
import tokyo.aieuo.mineflow.trigger.Triggers
import tokyo.aieuo.mineflow.trigger.event.EventManager
import tokyo.aieuo.mineflow.trigger.time.CheckTimeTriggerTask
import tokyo.aieuo.mineflow.utils.FormManager
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.utils.PlayerConfig
import tokyo.aieuo.mineflow.variable.VariableHelper
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

class Main : PluginBase() {

    lateinit var playerSettings: PlayerConfig
        private set

    private var loaded = false

    companion object {
        lateinit var instance: Main
            private set
        lateinit var pluginVersion: String
            private set

        lateinit var recipeManager: RecipeManager
            private set
        lateinit var commandManager: CommandManager
            private set
        lateinit var eventManager: EventManager
            private set
        lateinit var formManager: FormManager
            private set
        lateinit var variableHelper: VariableHelper
            private set
    }

    override fun onEnable() {
        instance = this
        pluginVersion = description.version

        playerSettings = PlayerConfig("${dataFolder.path}/player.yml")

        val serverLanguage = server.language.lang
        if (!config.exists("language")) {
            config.set("language", if (Language.isAvailableLanguage(serverLanguage)) serverLanguage else "eng")
            config.save()
        }

        Language.language = config.getString("language", "eng")
        if (!Language.isAvailableLanguage(Language.language)) {
            Language.getLoadErrorMessage(serverLanguage).forEach(logger::warning)
            server.pluginManager.disablePlugin(this)
            return
        }
        Language.getAvailableLanguages().forEach(Language::loadBaseMessage)

        server.commandMap.register(name, MineflowCommand())

        Economy.loadPlugin(this)

        EntityManager.init()
        Triggers.init()
        FlowItemFactory.init()

        commandManager = CommandManager(this, Config("${dataFolder.path}/commands.yml"))
        eventManager = EventManager(Config("${dataFolder.path}/events.yml"))
        formManager = FormManager(Config("${dataFolder.path}/forms.json"))

        variableHelper = VariableHelper(Config("${dataFolder.path}/variables.json"))

        recipeManager = RecipeManager("${dataFolder.path}/recipes/")
        recipeManager.loadRecipes()

        (EventListener()).registerEvents()

        if (!Files.exists(Path.of("${dataFolder.path}/imports/"))) File("${dataFolder.path}/imports/").mkdirs()

        CheckTimeTriggerTask.start()

        loaded = true
        server.pluginManager.callEvent(ServerStartEvent(this))
    }

    override fun onDisable() {
        if (!loaded) return

        recipeManager.saveAll()
        formManager.saveAll()
        variableHelper.saveAll()
    }
}
