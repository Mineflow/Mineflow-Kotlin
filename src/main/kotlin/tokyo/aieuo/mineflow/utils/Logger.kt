package tokyo.aieuo.mineflow.utils

import cn.nukkit.Player
import cn.nukkit.entity.Entity
import tokyo.aieuo.mineflow.Main

object Logger {

    fun warning(message: String, player: Entity? = null) {
        if (player is Player) {
            player.sendMessage("§e$message")
        } else {
            Main.instance.logger?.warning(message)
        }
    }

    fun info(message: String, player: Entity? = null) {
        if (player is Player) {
            player.sendMessage(message)
        } else {
            Main.instance.logger?.info(message)
        }
    }
}
