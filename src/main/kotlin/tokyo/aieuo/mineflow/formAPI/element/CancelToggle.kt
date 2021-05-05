package tokyo.aieuo.mineflow.formAPI.element


import cn.nukkit.Player
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponse
import tokyo.aieuo.mineflow.utils.Language

class CancelToggle(val onCancel: (() -> Unit)? = null, text: String = "@form.cancelAndBack", default: Boolean = false): Toggle(text, default) {

    constructor(onCancel: (() -> Unit)) : this(onCancel, "@form.cancelAndBack", false)

    override fun onFormSubmit(response: CustomFormResponse, player: Player) {
        if (response.getToggleResponse()) {
            response.ignoreResponse = true
            if (onCancel != null) {
                (onCancel)()
                response.interruptCallback = { true }
            }
        }
    }

    override fun jsonSerialize(): Map<String, Any?> {
        return mapOf(
            "type" to type.typeName,
            "text" to Language.replace(extraText) + reflectHighlight(Language.replace(text)),
            "default" to default,
            "mineflow" to mapOf(
                "type" to "cancelToggle"
            )
        )
    }
}