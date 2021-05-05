package tokyo.aieuo.mineflow.economy

import tokyo.aieuo.mineflow.Main

object Economy {

    var plugin: EconomyLoader? = null
        private set

    fun loadPlugin(owner: Main) {
        //
    }

    fun isPluginLoaded(): Boolean {
        return plugin !== null
    }
}