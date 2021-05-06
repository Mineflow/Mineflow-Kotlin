package tokyo.aieuo.mineflow.formAPI.element

import tokyo.aieuo.mineflow.utils.Language

class Slider(text: String, min: Float, max: Float, var step: Float = 1f, default: Float? = null) : ElementBase(text) {

    override val type = Element.Type.SLIDER

    var min: Float = minOf(min, max)
    var max: Float = maxOf(min, max)
    var default: Float = default ?: min

    override fun jsonSerialize(): Map<String, Any?> {
        return mapOf(
            "type" to type.typeName,
            "text" to Language.replace(extraText) + reflectHighlight(Language.replace(text)),
            "min" to min,
            "max" to max,
            "step" to step,
            "default" to minOf(maxOf(default, min), max),
        )
    }
}