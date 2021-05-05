package tokyo.aieuo.mineflow.event

import cn.nukkit.event.Cancellable
import cn.nukkit.event.entity.EntityDamageByEntityEvent
import cn.nukkit.event.plugin.PluginEvent
import tokyo.aieuo.mineflow.Main

class EntityAttackEvent(plugin: Main, val damageEvent: EntityDamageByEntityEvent): PluginEvent(plugin), Cancellable