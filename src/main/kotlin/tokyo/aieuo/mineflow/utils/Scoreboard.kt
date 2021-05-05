package tokyo.aieuo.mineflow.utils

import cn.nukkit.Player
import cn.nukkit.Server
import tokyo.aieuo.mineflow.packet.RemoveObjectivePacket
import tokyo.aieuo.mineflow.packet.SetDisplayObjectivePacket
import tokyo.aieuo.mineflow.packet.SetScorePacket
import tokyo.aieuo.mineflow.packet.types.ScorePacketEntry

class Scoreboard(val type: String = DISPLAY_SIDEBAR, val id: String = "objective", val displayName: String = "") {

    companion object {
        const val DISPLAY_SIDEBAR = "sidebar"
        const val DISPLAY_LIST = "list"
        const val DISPLAY_BELOWNAME = "belowname"
    }

    val scores: MutableMap<String, Int> = mutableMapOf()
    val scoreIds: MutableMap<String, Long> = mutableMapOf()
    var scoreId: Long = 0

    private val show = mutableSetOf<String>()

    fun existsScore(name: String): Boolean {
        return scores.containsKey(name)
    }

    fun setScore(name: String, value: Int, id: Long? = null) {
        scores[name] = value
        if (!scoreIds.containsKey(name)) scoreIds[name] = id ?: scoreId ++

        updateScoreToAllPlayer(name, value, scoreIds[name]!!)
    }

    fun setScoreName(name: String, score: Int) {
        val oldNames = scores.filterValues{ it == score }.keys
        if (oldNames.isEmpty()) {
            setScore(name, score)
            return
        }

        for (oldName in oldNames) {
            val id = scoreIds[oldName]
            removeScore(oldName)
            setScore(name, score, id)
        }
    }

    fun removeScoreName(score: Int) {
        val oldNames = scores.filterValues{ it == score }.keys
        if (oldNames.isEmpty()) return

        for (oldName in oldNames) {
            removeScore(oldName)
        }
        return
    }

    fun removeScoreNames(vararg scores: Int) {
        for (score in scores) {
            removeScoreName(score)
        }
    }

    fun getScore(name: String): Int? {
        return scores[name]
    }

    fun removeScore(name: String) {
        removeScoreFromAllPlayer(name)

        scores.remove(name)
        scoreIds.remove(name)
    }

    fun show(player: Player) {
        val pk = SetDisplayObjectivePacket()
        pk.displaySlot = type
        pk.objectiveName = id
        pk.displayName = displayName
        pk.criteriaName = "dummy"
        pk.sortOrder = 0
        player.dataPacket(pk)

        val pk2 = SetScorePacket()
        pk2.type = SetScorePacket.TYPE_CHANGE

        val entries = mutableListOf<ScorePacketEntry>()
        for ((name, score) in scores) {
            if (!scoreIds.containsKey(name)) continue

            val entry = ScorePacketEntry()
            entry.objectiveName = id
            entry.type = ScorePacketEntry.TYPE_FAKE_PLAYER
            entry.customName = name
            entry.score = score
            entry.scoreboardId = scoreIds[name]!!

            entries.add(entry)
        }
        pk2.entries = entries

        player.dataPacket(pk2)

        show.add(player.name)
    }

    fun hide(player: Player) {
        val pk = RemoveObjectivePacket()
        pk.objectiveName = id
        player.dataPacket(pk)

        show.remove(player.name)
    }

    fun removeScoreFromAllPlayer(scoreName: String) {
        for (name in show) {
            val player = Server.getInstance().getPlayerExact(name)
            if (player !is Player) continue
            removeScoreFromPlayer(player, scoreName)
        }
    }

    fun removeScoreFromPlayer(player: Player, scoreName: String) {
        if (player.name !in show) return

        val entry = ScorePacketEntry()
        entry.objectiveName = id
        entry.scoreboardId = scoreIds[scoreName]!!
        entry.score = 0

        val pk = SetScorePacket()
        pk.type = SetScorePacket.TYPE_REMOVE
        pk.entries = listOf(entry)
        player.dataPacket(pk)
    }

    fun updateScoreToAllPlayer(scoreName: String, score: Int, id: Long) {
        for (name in show) {
            val player = Server.getInstance().getPlayerExact(name)
            if (player !is Player) continue
            updateScoreToPlayer(player, scoreName, score, id)
        }
    }

    fun updateScoreToPlayer(player: Player, scoreName: String, value: Int, id: Long) {
        val entry = ScorePacketEntry()
        entry.objectiveName = id.toString()
        entry.type = ScorePacketEntry.TYPE_FAKE_PLAYER
        entry.customName = scoreName
        entry.score = value
        entry.scoreboardId = id

        val pk = SetScorePacket()
        pk.type = SetScorePacket.TYPE_CHANGE
        pk.entries = listOf(entry)
        player.dataPacket(pk)
    }
}