package tokyo.aieuo.mineflow.formAPI.element

import cn.nukkit.Player
import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponse
import tokyo.aieuo.mineflow.utils.Language

open class NumberInput(
    text: String,
    placeholder: String = "",
    default: String = "",
    required: Boolean = false,
    var min: Double? = null,
    var max: Double? = null,
    val excludes: List<Double> = listOf()
) : Input(text, placeholder, default, required) {

    override fun onFormSubmit(response: CustomFormResponse, player: Player) {
        super.onFormSubmit(response, player)
        val data = response.getInputResponse()

        if (data == "" || Main.variableHelper.containsVariable(data)) return

        val number = data.toDoubleOrNull()
        if (number === null) {
            response.addError(Language.get("action.error.notNumber", listOf(data)))
        } else if (min !== null && number < min!!) {
            response.addError(Language.get("action.error.lessValue", listOf(min.toString(), data)))
        } else if (max !== null && number > max!!) {
            response.addError(Language.get("action.error.overValue", listOf(max.toString(), data)))
        } else if (excludes.isNotEmpty() && excludes.contains(number)) {
            response.addError(Language.get("action.error.excludedNumber", listOf(excludes.joinToString(";"), data)))
        }
    }

    override fun serializeExtraData(): Map<String, Any?> {
        return mapOf(
            "type" to "number",
            "required" to required,
            "min" to min,
            "max" to max,
            "excludes" to excludes,
        )
    }
}