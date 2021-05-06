package tokyo.aieuo.mineflow.packet

import cn.nukkit.network.protocol.DataPacket

class RemoveObjectivePacket : DataPacket() {

    override fun pid(): Byte = 0x6a.toByte()

    var objectiveName = ""

    override fun decode() {
        //
        objectiveName = string
    }

    override fun encode() {
        reset()
        putString(objectiveName)
    }
}
