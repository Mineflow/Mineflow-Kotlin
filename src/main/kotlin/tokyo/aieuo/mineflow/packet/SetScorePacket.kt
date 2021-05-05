package tokyo.aieuo.mineflow.packet

import cn.nukkit.network.protocol.DataPacket
import tokyo.aieuo.mineflow.packet.types.ScorePacketEntry

class SetScorePacket : DataPacket() {

    companion object {
        const val TYPE_CHANGE: Byte = 0
        const val TYPE_REMOVE: Byte = 1
    }

    override fun pid(): Byte = 0x6c.toByte()

    var type: Byte = TYPE_CHANGE
    var entries = listOf<ScorePacketEntry>()

    override fun decode() {
        //
    }

    override fun encode() {
        reset()
        putByte(type)
        putUnsignedVarInt(entries.size.toLong())
        for (entry in entries) {
            putVarLong(entry.scoreboardId)
            putString(entry.objectiveName)
            putLInt(entry.score)
            if (type != TYPE_REMOVE) {
                putByte(entry.type)
                when (entry.type) {
                    ScorePacketEntry.TYPE_PLAYER -> putUnsignedVarLong(entry.entityUniqueId)
                    ScorePacketEntry.TYPE_ENTITY -> putUnsignedVarLong(entry.entityUniqueId)
                    ScorePacketEntry.TYPE_FAKE_PLAYER -> putString(entry.customName)
                }
            }
        }
    }
}
