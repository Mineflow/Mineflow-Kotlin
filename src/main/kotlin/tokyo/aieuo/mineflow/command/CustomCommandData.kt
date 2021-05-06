package tokyo.aieuo.mineflow.command

data class CustomCommandData(
    val command: String,
    var permission: String,
    var description: String,
    val subCommands: Map<String, Any> = mapOf()
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "command" to command,
            "permission" to permission,
            "description" to description,
            "subCommands" to subCommands,
        )
    }
}
