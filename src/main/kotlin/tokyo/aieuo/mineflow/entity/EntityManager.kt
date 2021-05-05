package tokyo.aieuo.mineflow.entity

import cn.nukkit.entity.Entity

object EntityManager {
    fun init() {
        Entity.registerEntity("MineflowHuman", MineflowHuman::class.java, true)
    }
}