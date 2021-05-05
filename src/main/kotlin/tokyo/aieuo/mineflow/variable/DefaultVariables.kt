package tokyo.aieuo.mineflow.variable

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.block.Block
import cn.nukkit.blockentity.BlockEntitySign
import cn.nukkit.entity.Entity
import tokyo.aieuo.mineflow.utils.microtime
import tokyo.aieuo.mineflow.variable.obj.BlockObjectVariable
import tokyo.aieuo.mineflow.variable.obj.EntityObjectVariable
import tokyo.aieuo.mineflow.variable.obj.PlayerObjectVariable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DefaultVariables {

    fun getServerVariables(): Map<String, Variable<Any>> {
        val server = Server.getInstance()
        val onlines = server.onlinePlayers.map { (_, player) -> PlayerObjectVariable(player) }
        val date = LocalDateTime.now()
        return mapOf(
            "server_name" to StringVariable(server.name),
            "microtime" to NumberVariable(microtime()),
            "time" to StringVariable(date.format(DateTimeFormatter.ofPattern("HH:mm:ss"))),
            "date" to StringVariable(date.format(DateTimeFormatter.ofPattern("MM/dd"))),
            "default_world" to StringVariable(server.defaultLevel.folderName),
            "onlines" to ListVariable(onlines),
            "ops" to ListVariable(server.ops.all.map { (k, _) -> StringVariable(k) } ),
        )
    }

    fun getEntityVariables(target: Entity, name: String = "target"): Map<String, Variable<Any>> {
        if (target is Player) return getPlayerVariables(target, name)
        return mapOf(name to EntityObjectVariable(target, target.nameTag))
    }

    fun getPlayerVariables(target: Player, name: String = "target"): Map<String, Variable<Any>> {
        return mapOf(name to PlayerObjectVariable(target, target.name))
    }

    fun getBlockVariables(block: Block, name: String = "block"): Map<String, Variable<Any>> {
        val variables = mutableMapOf<String, Variable<Any>>(
            name to BlockObjectVariable(block, "${block.id}:${block.damage}")
        )
        val tile = block.level.getBlockEntity(block)
        if (tile is BlockEntitySign) {
            variables["sign_lines"] = ListVariable(tile.text.map { StringVariable(it) })
        }
        return variables
    }

    fun getCommandVariables(command: String): Map<String, Variable<Any>> {
        val commands = ArrayDeque(command.split(" "))
        return mapOf(
            "cmd" to StringVariable(commands.removeFirstOrNull() ?: command),
            "args" to ListVariable(commands.map { StringVariable(it) }.toMutableList()),
        )
    }
}