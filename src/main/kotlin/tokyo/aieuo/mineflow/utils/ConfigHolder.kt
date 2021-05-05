package tokyo.aieuo.mineflow.utils

import cn.nukkit.utils.Config
import tokyo.aieuo.mineflow.Main
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

object ConfigHolder {

    val configs: MutableMap<String, Config> = mutableMapOf()

    fun existsConfigFile(_name: String): Boolean {
        val name = _name.replace(Regex("[.¥/:?<>|*\"]"), "")
        if (configs.containsKey(name)) return true

        val path = "${Main.instance.dataFolder.path}/configs/${name}.yml"
        return Files.exists(Path.of(path))
    }

    fun getConfig(_name: String): Config {
        val name = _name.replace(Regex("[.¥/:?<>|*\"]"), "")
        val config = configs[name]
        if (config !== null) return config

        val dir = "${Main.instance.dataFolder.path}/configs"
        if (!Files.exists(Path.of(dir))) File(dir).mkdirs()

        configs[name] = Config("${dir}/${name}.yml")
        return configs[name]!!
    }

    fun setConfig(_name: String, data: LinkedHashMap<String, Any?>, save: Boolean = false) {
        val name = _name.replace(Regex("[.¥/:?<>|*\"]"), "")

        val dir = "${Main.instance.dataFolder.path}/configs"
        if (!Files.exists(Path.of(dir))) File(dir).mkdirs()

        val config = Config("${dir}/${name}.yml")
        config.setAll(data)
        if (save) config.save()
        configs[name] = config
    }
}