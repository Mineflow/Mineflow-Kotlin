package tokyo.aieuo.mineflow.formAPI.element

import tokyo.aieuo.mineflow.utils.Language

class Label(text: String): ElementBase(text) {

    override val type = Element.Type.LABEL

    override fun jsonSerialize(): Map<String, Any?> {
        return mapOf(
            "type" to type.typeName,
            "text" to Language.replace(extraText) + reflectHighlight(Language.replace(text)),
        )
    }
}