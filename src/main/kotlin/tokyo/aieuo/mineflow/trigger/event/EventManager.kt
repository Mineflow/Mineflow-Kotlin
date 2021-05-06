package tokyo.aieuo.mineflow.trigger.event

import cn.nukkit.event.Event
import cn.nukkit.event.block.BlockBreakEvent
import cn.nukkit.event.block.BlockPlaceEvent
import cn.nukkit.event.block.SignChangeEvent
import cn.nukkit.event.entity.EntityDamageEvent
import cn.nukkit.event.entity.EntityDeathEvent
import cn.nukkit.event.entity.EntityLevelChangeEvent
import cn.nukkit.event.inventory.CraftItemEvent
import cn.nukkit.event.inventory.FurnaceBurnEvent
import cn.nukkit.event.inventory.InventoryPickupItemEvent
import cn.nukkit.event.level.LevelLoadEvent
import cn.nukkit.event.player.*
import cn.nukkit.utils.Config
import tokyo.aieuo.mineflow.event.EntityAttackEvent
import tokyo.aieuo.mineflow.event.PlayerExhaustEvent
import tokyo.aieuo.mineflow.event.ProjectileHitEntityEvent
import tokyo.aieuo.mineflow.event.ServerStartEvent
import tokyo.aieuo.mineflow.trigger.TriggerHolder
import kotlin.reflect.KClass

class EventManager(val setting: Config) {

    val all: MutableMap<Pair<String, String>, EventTrigger> = mutableMapOf()
    val events: MutableMap<String, Boolean> = mutableMapOf()
    private val eventListener = EventTriggerListener()

    init {
        addDefaultTriggers()
    }

    fun addDefaultTriggers() {
        addTrigger(BlockBreakEventTrigger(), BlockBreakEvent::class, true)
        addTrigger(BlockPlaceEventTrigger(), BlockPlaceEvent::class, true)
        addTrigger(CraftItemEventTrigger(), CraftItemEvent::class, true)
        addTrigger(EntityAttackEventTrigger(), EntityAttackEvent::class, true)
        addTrigger(EntityDamageEventTrigger(), EntityDamageEvent::class, true)
        addTrigger(EntityLevelChangeEventTrigger(), EntityLevelChangeEvent::class, true)
        addTrigger(FurnaceBurnEventTrigger(), FurnaceBurnEvent::class, false)
        addTrigger(LevelLoadEventTrigger(), LevelLoadEvent::class, false)
        addTrigger(PlayerBedEnterEventTrigger(), PlayerBedEnterEvent::class, false)
        addTrigger(PlayerChatEventTrigger(), PlayerChatEvent::class, true)
        addTrigger(PlayerCommandPreprocessEventTrigger(), PlayerCommandPreprocessEvent::class, true)
        addTrigger(PlayerDeathEventTrigger(), PlayerDeathEvent::class, true)
        addTrigger(EntityDeathEventTrigger(), EntityDeathEvent::class, false)
        addTrigger(PlayerDropItemEventTrigger(), PlayerDropItemEvent::class, false)
        addTrigger(PlayerExhaustEventTrigger(), PlayerExhaustEvent::class, false)
        addTrigger(PlayerInteractEventTrigger(), PlayerInteractEvent::class, true)
        addTrigger(PlayerItemConsumeEventTrigger(), PlayerItemConsumeEvent::class, true)
        addTrigger(PlayerJoinEventTrigger(), PlayerJoinEvent::class, true)
        addTrigger(PlayerMoveEventTrigger(), PlayerMoveEvent::class, false)
        addTrigger(PlayerQuitEventTrigger(), PlayerQuitEvent::class, true)
        addTrigger(PlayerToggleFlightEventTrigger(), PlayerToggleFlightEvent::class, true)
        addTrigger(PlayerToggleSneakEventTrigger(), PlayerToggleSneakEvent::class, true)
        addTrigger(PlayerToggleSprintEventTrigger(), PlayerToggleSprintEvent::class, false)
        addTrigger(ProjectileHitEntityEventTrigger(), ProjectileHitEntityEvent::class, false)
        addTrigger(SignChangeEventTrigger(), SignChangeEvent::class, false)
        addTrigger(ServerStartEventTrigger(), ServerStartEvent::class, true)
        addTrigger(InventoryPickupItemEventTrigger(), InventoryPickupItemEvent::class, false)
        addTrigger(PlayerChangeSkinEventTrigger(), PlayerChangeSkinEvent::class, false)
    }

    fun addTrigger(trigger: EventTrigger, eventClass: Class<out Event>, defaultEnabled: Boolean) {
        trigger.enabled = setting.getBoolean(trigger.key, defaultEnabled)

        if (trigger.enabled) {
            eventListener.registerEvent(eventClass)
        }

        all[trigger.key to trigger.subKey] = trigger
        events[trigger.key] = trigger.enabled
    }

    fun addTrigger(trigger: EventTrigger, eventClass: KClass<out Event>, defaultEnabled: Boolean) {
        addTrigger(trigger, eventClass.java, defaultEnabled)
    }

    fun getTrigger(_key: String, subKey: String = ""): EventTrigger? {
        val key = _key.replace("\\", ".")
            .replace("pocketmine", "cn.nukkit")
            .let {
                if (it.startsWith("aieuo.")) it.replace("aieuo", "tokyo.aieuo")
                else it
            }
        return all[key to subKey]
    }

    fun existsTrigger(key: String, subKey: String = ""): Boolean {
        return all.containsKey(key to subKey)
    }

    fun getEnabledEvents() = events.filterValues { it }

    fun enableEvent(event: String) {
        setting.set(event, true)
        setting.save()

        events[event] = true
    }

    fun disableEvent(event: String) {
        setting.set(event, false)
        setting.save()

        events[event] = false
    }

    fun getAssignedRecipes(event: String): MutableMap<String, MutableList<String>> {
        val recipes = mutableMapOf<String, MutableList<String>>()
        val containers = TriggerHolder.getRecipesWithSubKey(EventTrigger.create(event))
        for ((name, container) in containers) {
            for (recipe in container.getAllRecipe().values) {
                val path = "${recipe.group}/${recipe.name}"
                if (!recipes.containsKey(path)) recipes[path] = mutableListOf()
                recipes[path]?.add(name)
            }
        }
        return recipes
    }
}