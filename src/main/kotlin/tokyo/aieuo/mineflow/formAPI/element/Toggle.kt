package tokyo.aieuo.mineflow.formAPI.element

import tokyo.aieuo.mineflow.utils.Language

open class Toggle(text: String, var default: Boolean = false): ElementBase(text) {

    override val type = Element.Type.TOGGLE

    override fun jsonSerialize(): Map<String, Any?> {
        return mapOf(
            "type" to type.typeName,
            "text" to Language.replace(extraText) + reflectHighlight(Language.replace(text)),
            "default" to default,
        )
    }
}