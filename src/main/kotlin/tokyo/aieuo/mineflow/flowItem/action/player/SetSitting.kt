package tokyo.aieuo.mineflow.flowItem.action.player

import cn.nukkit.Player
import cn.nukkit.entity.Entity
import cn.nukkit.entity.data.EntityMetadata
import cn.nukkit.network.protocol.AddEntityPacket
import cn.nukkit.network.protocol.RemoveEntityPacket
import cn.nukkit.network.protocol.types.EntityLink
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.PlayerFlowItem
import tokyo.aieuo.mineflow.flowItem.base.PositionFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.PlayerVariableDropdown
import tokyo.aieuo.mineflow.formAPI.element.mineflow.PositionVariableDropdown
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language

class SetSitting(player: String = "", position: String = "") : FlowItem(), PlayerFlowItem, PositionFlowItem {

    override val id = FlowItemIds.SET_SITTING

    override val nameTranslationKey = "action.setSitting.name"
    override val detailTranslationKey = "action.setSitting.detail"
    override val detailDefaultReplaces = listOf("player", "position")

    override val category = Category.PLAYER

    override var playerVariableNames: MutableMap<String, String> = mutableMapOf()
    override var positionVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setPlayerVariableName(player)
        setPositionVariableName(position)
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getPlayerVariableName(), getPositionVariableName()))
    }

    override fun isDataValid(): Boolean {
        return getPlayerVariableName() != "" && getPositionVariableName() != ""
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val player = getPlayer(source)
        throwIfInvalidPlayer(player)

        val position = getPosition(source)

        leave(player)

        val pk = AddEntityPacket()
        pk.entityRuntimeId = Entity.entityCount++
        pk.id = "minecraft:minecart"
        pk.x = position.x.toFloat()
        pk.y = position.y.toFloat()
        pk.z = position.z.toFloat()
        pk.links = arrayOf(EntityLink(pk.entityRuntimeId, player.id, EntityLink.TYPE_RIDER, false, true))
        pk.metadata = EntityMetadata().apply {
            putLong(Entity.DATA_FLAGS, 0)
        }
        player.dataPacket(pk)

        entityIds[player] = pk.entityRuntimeId
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            PlayerVariableDropdown(variables, getPlayerVariableName()),
            PositionVariableDropdown(variables, getPositionVariableName()),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setPlayerVariableName(contents.getString(0))
        setPositionVariableName(contents.getString(1))
    }

    override fun serializeContents(): List<Any> {
        return listOf(getPlayerVariableName(), getPositionVariableName())
    }

    override fun clone(): SetSitting {
        val item = super.clone() as SetSitting
        item.playerVariableNames = playerVariableNames.toMutableMap()
        item.positionVariableNames = positionVariableNames.toMutableMap()
        return item
    }

    companion object {
        val entityIds: MutableMap<Entity, Long> = mutableMapOf()

        fun leave(player: Player) {
            val id = entityIds[player] ?: return

            entityIds.remove(player)
            if (player.isOnline) player.dataPacket(RemoveEntityPacket().apply {
                eid = id
            })
        }
    }
}
