package tokyo.aieuo.mineflow.formAPI.element

import cn.nukkit.Player
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponse
import tokyo.aieuo.mineflow.utils.Language

open class Input(text: String, var placeholder: String = "", var default: String = "", var required: Boolean = false) :
    ElementBase(text) {

    override val type = Element.Type.INPUT

    override fun onFormSubmit(response: CustomFormResponse, player: Player) {
        val data = response.getInputResponse().replace("\\n", "\n")

        if (required && data == "") {
            response.addError("@form.insufficient")
        }

        if (response.getInputResponse() !== data) response.overrideResponse(data)
    }

    override fun jsonSerialize(): Map<String, Any?> {
        return mapOf(
            "type" to type.typeName,
            "text" to Language.replace(extraText) + reflectHighlight(Language.replace(text)),
            "placeholder" to Language.replace(placeholder),
            "default" to Language.replace(default).replace("\n", "\\n"),
            "mineflow" to serializeExtraData(),
        )
    }

    open fun serializeExtraData(): Map<String, Any?> {
        return mapOf(
            "type" to "text",
            "required" to required,
        )
    }
}