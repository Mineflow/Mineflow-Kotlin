package tokyo.aieuo.mineflow.trigger.event

import cn.nukkit.entity.Entity
import cn.nukkit.event.Event
import cn.nukkit.event.player.PlayerChangeSkinEvent
import tokyo.aieuo.mineflow.variable.DummyVariable

class PlayerChangeSkinEventTrigger(subKey: String = ""): EventTrigger(PlayerChangeSkinEvent::class, subKey) {

    override fun getTargetEntity(event: Event): Entity? = getTargetEntity(event as PlayerChangeSkinEvent)

    fun getTargetEntity(event: PlayerChangeSkinEvent): Entity? {
        return event.player
    }

    override fun getVariablesDummy(): Map<String, DummyVariable<DummyVariable.Type>> {
        return mapOf(
            "target" to DummyVariable(DummyVariable.Type.PLAYER)
        )
    }
}