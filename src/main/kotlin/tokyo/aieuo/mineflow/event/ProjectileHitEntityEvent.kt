package tokyo.aieuo.mineflow.event

import cn.nukkit.entity.Entity
import cn.nukkit.event.Event
import cn.nukkit.level.MovingObjectPosition

class ProjectileHitEntityEvent(val entity: Entity, val movingObjectPosition: MovingObjectPosition, val entityHit: Entity): Event()
