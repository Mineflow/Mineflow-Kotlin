package tokyo.aieuo.mineflow.utils

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.entity.Entity

object EntityHolder {

    val entities: MutableMap<Long, Entity?> = mutableMapOf()

    fun getPlayerByName(name: String): Player? {
        val player = Server.getInstance().getPlayer(name)
        if (player is Player) entities[player.id] = player
        return player
    }

    fun findEntity(id: Long): Entity? {
        if (id > Entity.entityCount) return null
        var entity = entities[id]
        if (entities.containsKey(id) && entity !== null) {
            if (!entity.isAlive || entity.isClosed || (entity is Player && !entity.isOnline)) {
                entities[id] = null
                return null
            }
            return entity
        }

        val levels = Server.getInstance().levels
        for (level in levels.values) {
            entity = level.getEntity(id)
            if (entity is Entity) break
        }

        entities[id] = entity
        return entity
    }

    fun isPlayer(id: Long): Boolean {
        val entity = findEntity(id)
        return entity is Player
    }

    fun isActive(id: Long): Boolean {
        val entity = findEntity(id) ?: return false
        return entity.isAlive && !entity.isClosed && !(entity is Player && !entity.isOnline)
    }
}