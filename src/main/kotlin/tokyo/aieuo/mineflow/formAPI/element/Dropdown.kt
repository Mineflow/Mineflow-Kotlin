package tokyo.aieuo.mineflow.formAPI.element

import tokyo.aieuo.mineflow.utils.Language

open class Dropdown(text: String, var options: List<String> = listOf(), var default: Int = 0) : ElementBase(text) {

    override val type = Element.Type.DROPDOWN

    override fun jsonSerialize(): Map<String, Any?> {
        return mapOf(
            "type" to type.typeName,
            "text" to Language.replace(extraText) + reflectHighlight(Language.replace(text)),
            "options" to options,
            "default" to default,
        )
    }
}