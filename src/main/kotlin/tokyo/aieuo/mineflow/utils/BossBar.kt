package tokyo.aieuo.mineflow.utils

import cn.nukkit.Player
import cn.nukkit.entity.Entity
import cn.nukkit.entity.data.EntityMetadata
import cn.nukkit.entity.mob.EntityShulker
import cn.nukkit.network.protocol.AddEntityPacket
import cn.nukkit.network.protocol.BossEventPacket
import cn.nukkit.network.protocol.RemoveEntityPacket

class BossBar(var title: String, var max: Float = 1f, percentage: Float = 1f) {

    var percentage: Float = percentage
        set(value) {
            field = if (field > max) max else field
        }

    private val entityId: Long = Entity.entityCount ++

    companion object {
        val bars: MutableMap<Pair<String, String>, BossBar> = mutableMapOf()

        fun add(player: Player, id: String, title: String, max: Float, per: Float) {
            if (bars.containsKey(player.name to id)) remove(player, id)

            val bar = BossBar(title, max, per)
            bars[player.name to id] = bar

            val pk = AddEntityPacket()
            pk.entityUniqueId = bar.entityId
            pk.entityRuntimeId = bar.entityId
            pk.id = AddEntityPacket.LEGACY_IDS[EntityShulker.NETWORK_ID]
            pk.metadata = EntityMetadata().apply {
                putLong(Entity.DATA_FLAGS, 0)
                putString(Entity.DATA_NAMETAG, title)
            }
            pk.x = 0f
            pk.y = 0f
            pk.z = 0f
            pk.speedX = 0f
            pk.speedY = 0f
            pk.speedZ = 0f
            player.dataPacket(pk)

            val pk2 = BossEventPacket()
            pk2.bossEid = bar.entityId
            pk2.type = BossEventPacket.TYPE_SHOW
            pk2.title = title
            pk2.healthPercent = per
            pk2.color = 0
            pk2.overlay = 0
            pk2.unknown = 0
            player.dataPacket(pk2)
        }

        fun remove(player: Player, id: String): Boolean {
            val bar = bars[player.name to id] ?: return false
            val pk = BossEventPacket()
            pk.bossEid = bar.entityId
            pk.type = BossEventPacket.TYPE_HIDE
            player.dataPacket(pk)

            val pk2 = RemoveEntityPacket()
            pk2.eid = bar.entityId
            player.dataPacket(pk2)

            bars.remove(player.name to id)
            return true
        }
    }
}