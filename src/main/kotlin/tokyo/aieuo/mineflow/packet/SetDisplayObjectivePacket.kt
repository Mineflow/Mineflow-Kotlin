package tokyo.aieuo.mineflow.packet

import cn.nukkit.network.protocol.DataPacket

class SetDisplayObjectivePacket : DataPacket() {

    override fun pid(): Byte = 0x6b.toByte()

    var displaySlot = ""
    var objectiveName = ""
    var displayName = ""
    var criteriaName = ""
    var sortOrder = 0

    override fun decode() {
        //
    }

    override fun encode() {
        reset()
        putString(displaySlot)
        putString(objectiveName)
        putString(displayName)
        putString(criteriaName)
        putVarInt(sortOrder)
    }
}
