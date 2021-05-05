package tokyo.aieuo.mineflow.event

import cn.nukkit.event.plugin.PluginEvent
import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.utils.microtime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ServerStartEvent(plugin: Main): PluginEvent(plugin) {

    private val microtime = microtime()
    private val date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

    fun getMicrotime(): Double {
        return microtime
    }

    fun getDate(): String {
        return date
    }
}