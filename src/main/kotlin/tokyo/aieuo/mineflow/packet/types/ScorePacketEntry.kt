package tokyo.aieuo.mineflow.packet.types

class ScorePacketEntry {

    companion object {
        const val TYPE_PLAYER: Byte = 1.toByte()
        const val TYPE_ENTITY: Byte = 2.toByte()
        const val TYPE_FAKE_PLAYER: Byte = 3.toByte()
    }

    var scoreboardId: Long = 0
    var objectiveName = ""
    var score = 0

    var type: Byte = 0

    var entityUniqueId: Long = 0
    var customName: String? = null
}
