package tokyo.aieuo.mineflow.formAPI.element.mineflow

import cn.nukkit.Server
import tokyo.aieuo.mineflow.formAPI.element.Button
import tokyo.aieuo.mineflow.formAPI.utils.ButtonImage
import tokyo.aieuo.mineflow.utils.Language

class CommandButton(var command: String, text: String? = null, image: ButtonImage? = null) :
    Button(text ?: "/${command}", { Server.getInstance().dispatchCommand(it, command) }, image) {

    override val type = TYPE_COMMAND

    override val skipIfCallOnClick: Boolean = false

    override fun toString(): String {
        return Language.get("form.form.formMenu.list.commandButton", listOf(text, command))
    }

    override fun jsonSerialize(): Map<String, Any?> {
        return mapOf(
            "text" to Language.replace(text),
            "id" to getUUID(),
            "image" to image,
            "mineflow" to mapOf(
                "command" to command
            ),
        )
    }
}