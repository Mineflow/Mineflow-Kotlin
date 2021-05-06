package tokyo.aieuo.mineflow

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.command.Command
import cn.nukkit.event.EventHandler
import cn.nukkit.event.Listener
import cn.nukkit.event.entity.EntityDamageByEntityEvent
import cn.nukkit.event.entity.EntityLevelChangeEvent
import cn.nukkit.event.entity.EntityTeleportEvent
import cn.nukkit.event.entity.ProjectileHitEvent
import cn.nukkit.event.player.*
import cn.nukkit.event.server.DataPacketReceiveEvent
import cn.nukkit.network.protocol.InteractPacket
import cn.nukkit.network.protocol.ModalFormResponsePacket
import tokyo.aieuo.mineflow.event.EntityAttackEvent
import tokyo.aieuo.mineflow.event.PlayerExhaustEvent
import tokyo.aieuo.mineflow.event.ProjectileHitEntityEvent
import tokyo.aieuo.mineflow.flowItem.action.player.SetSitting
import tokyo.aieuo.mineflow.formAPI.FormAPI
import tokyo.aieuo.mineflow.recipe.Recipe
import tokyo.aieuo.mineflow.trigger.TriggerHolder
import tokyo.aieuo.mineflow.trigger.block.BlockTrigger
import tokyo.aieuo.mineflow.trigger.command.CommandTrigger
import tokyo.aieuo.mineflow.ui.trigger.BlockTriggerForm
import tokyo.aieuo.mineflow.utils.Session

class EventListener : Listener {

    fun registerEvents() {
        Server.getInstance().pluginManager.registerEvents(this, Main.instance)
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        Session.createSession(event.player)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        Session.destroySession(event.player)
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        if (event.action !== PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK && event.action !== PlayerInteractEvent.Action.RIGHT_CLICK_AIR) return

        val player = event.player
        val block = event.block
        val session = Session.getSession(player)
        val position = "${block.x},${block.y},${block.z},${block.level.folderName}"

        if (player.isOp && session.exists("blockTriggerAction")) {
            val type = session.getString("blockTriggerAction")
            session.remove("blockTriggerAction")

            when (type) {
                "add" -> {
                    val recipe = session.getObject<Recipe>("blockTriggerRecipe")
                    if (recipe === null) return

                    val trigger = BlockTrigger.create(position)
                    if (recipe.existsTrigger(trigger)) {
                        BlockTriggerForm.sendAddedTriggerMenu(player, recipe, trigger, listOf("@trigger.alreadyExists"))
                        return
                    }
                    recipe.addTrigger(trigger)
                    BlockTriggerForm.sendAddedTriggerMenu(player, recipe, trigger, listOf("@trigger.add.success"))
                }
            }
            return
        }

        val trigger = BlockTrigger.create(position)
        if (TriggerHolder.existsRecipe(trigger)) {
            val recipes = TriggerHolder.getRecipes(trigger)
            val variables = trigger.getVariables(block)
            recipes?.executeAll(player, variables, event)
        }
    }

    @EventHandler
    fun command(event: PlayerCommandPreprocessEvent) {
        val sender = event.player
        if (event.isCancelled) return

        val message = event.message

        if (message[0].toString() != "/") return

        val commands = ArrayDeque(message.substring(1).split(" "))

        val count = commands.size
        val origin = commands[0]
        val command = Server.getInstance().commandMap.getCommand(origin)
        if (command !is Command || !command.testPermissionSilent(sender)) return

        for (i in 0 until count) {
            val cmd = commands.joinToString(" ")
            val trigger = CommandTrigger.create(origin, cmd)
            if (TriggerHolder.existsRecipe(trigger)) {
                val recipes = TriggerHolder.getRecipes(trigger)
                val variables = trigger.getVariables(event.message.substring(1))
                recipes?.executeAll(sender, variables, event)
                break
            }
            commands.removeLast()
        }
    }

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        val player = event.entity
        if (player is Player) SetSitting.leave(player)
    }

    @EventHandler
    fun onLevelChange(event: EntityLevelChangeEvent) {
        val player = event.entity
        if (player is Player) SetSitting.leave(player)
    }

    @EventHandler
    fun receive(event: DataPacketReceiveEvent) {
        val pk = event.packet
        val player = event.player
        if ((pk is InteractPacket) && pk.action == InteractPacket.ACTION_VEHICLE_EXIT) {
            SetSitting.leave(player)
        }
        if (pk is ModalFormResponsePacket) {
            FormAPI.onReceiveForm(player, pk.formId, pk.data.trim())
        }
    }

    @EventHandler
    fun teleport(event: EntityTeleportEvent) {
        val player = event.entity
        if (player is Player) SetSitting.leave(player)
    }

    @EventHandler
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        val ev = EntityAttackEvent(Main.instance, event)
        Server.getInstance().pluginManager.callEvent(ev)

        if (ev.isCancelled) {
            event.setCancelled()
        }
    }

    @EventHandler
    fun onExhaust(event: PlayerFoodLevelChangeEvent) {
        Server.getInstance().pluginManager.callEvent(PlayerExhaustEvent(event.foodLevel))
    }

    @EventHandler
    fun onProjectileHit(event: ProjectileHitEvent) {
        val pos = event.movingObjectPosition
        if (pos.typeOfHit == 1) {
            Server.getInstance().pluginManager.callEvent(
                ProjectileHitEntityEvent(
                    event.entity,
                    event.movingObjectPosition,
                    pos.entityHit
                )
            )
        }
    }
}
