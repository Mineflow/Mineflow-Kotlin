package tokyo.aieuo.mineflow.formAPI.element

import cn.nukkit.Player
import cn.nukkit.utils.TextFormat
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponse
import tokyo.aieuo.mineflow.utils.JsonSerializable

interface Element: JsonSerializable {

    val type: Type
    var text: String

    var extraText: String
    var highlight: TextFormat?

    enum class Type(val typeName: String) {
        LABEL("label"),
        INPUT("input"),
        SLIDER("slider"),
        STEP_SLIDER("step_slider"),
        DROPDOWN("dropdown"),
        TOGGLE("toggle");
    }

    fun reflectHighlight(text: String): String {
        if (highlight === null) return text
        return highlight.toString() + text.replace(Regex("/§[a-f0-9]/u"), "")
    }

    fun onFormSubmit(response: CustomFormResponse, player: Player) {
    }
}