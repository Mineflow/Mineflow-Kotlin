package tokyo.aieuo.mineflow.formAPI.element

import tokyo.aieuo.mineflow.utils.Language

class StepSlider(text: String, options: MutableList<String> = mutableListOf(), default: Int = 0) :
    Dropdown(text, options, default) {

    override val type = Element.Type.STEP_SLIDER

    override fun jsonSerialize(): Map<String, Any?> {
        return mapOf(
            "type" to type.typeName,
            "text" to Language.replace(extraText) + reflectHighlight(Language.replace(text)),
            "steps" to options,
            "default" to default,
        )
    }
}