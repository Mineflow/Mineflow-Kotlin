package tokyo.aieuo.mineflow.trigger.event

import cn.nukkit.Player
import cn.nukkit.entity.Entity
import cn.nukkit.event.Event
import cn.nukkit.event.entity.EntityDamageByEntityEvent
import cn.nukkit.event.player.PlayerDeathEvent
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.VariableMap
import tokyo.aieuo.mineflow.variable.DefaultVariables
import tokyo.aieuo.mineflow.variable.DummyVariable

class PlayerDeathEventTrigger(subKey: String = "") : EventTrigger(PlayerDeathEvent::class, subKey) {

    override fun getTargetEntity(event: Event): Entity? = getTargetEntity(event as PlayerDeathEvent)
    override fun getVariables(event: Event): VariableMap = getVariables(event as PlayerDeathEvent)

    fun getTargetEntity(event: PlayerDeathEvent): Entity? {
        return event.entity
    }

    fun getVariables(event: PlayerDeathEvent): VariableMap {
        val target = event.entity
        var variables = DefaultVariables.getPlayerVariables(target)
        val cause = target.lastDamageCause
        if (cause is EntityDamageByEntityEvent) {
            val killer = cause.damager
            if (killer is Player) {
                variables = variables + DefaultVariables.getPlayerVariables(killer, "killer")
            }
        }
        return variables
    }

    override fun getVariablesDummy(): DummyVariableMap {
        return mapOf(
            "target" to DummyVariable(DummyVariable.Type.PLAYER),
            "killer" to DummyVariable(DummyVariable.Type.PLAYER),
        )
    }
}