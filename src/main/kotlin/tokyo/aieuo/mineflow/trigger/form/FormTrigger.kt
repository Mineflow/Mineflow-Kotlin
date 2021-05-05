package tokyo.aieuo.mineflow.trigger.form

import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.formAPI.CustomForm
import tokyo.aieuo.mineflow.formAPI.ListForm
import tokyo.aieuo.mineflow.formAPI.ModalForm
import tokyo.aieuo.mineflow.formAPI.element.Button
import tokyo.aieuo.mineflow.formAPI.element.Dropdown
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.trigger.Trigger
import tokyo.aieuo.mineflow.trigger.Triggers
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.*

class FormTrigger(key: String, subKey: String = ""): Trigger(Triggers.FORM, key, subKey) {

    companion object {
        fun create(key: String, subKey: String = ""): FormTrigger {
            return FormTrigger(key, subKey)
        }
    }

    fun getVariables(form: ModalForm, data: Boolean): Map<String, Variable<Any>> {
        val variable = MapVariable(mapOf(
            "data" to BoolVariable(data),
            "button1" to MapVariable(mapOf(
                "selected" to BoolVariable(data),
                "text" to StringVariable(form.getButton1Text()),
            ),  form.getButton1Text()),
            "button2" to MapVariable(mapOf(
                "selected" to BoolVariable(!data),
                "text" to StringVariable(form.getButton2Text()),
            ), form.getButton2Text()),
        ))
        return mapOf("form" to variable)
    }

    fun getVariables(form: ListForm, data: Int): Map<String, Variable<Any>> {
        val variable = MapVariable(mapOf(
            "data" to NumberVariable(data),
            "button" to StringVariable(form.getButton(data)?.text ?: ""),
        ))
        return mapOf("form" to variable)
    }

    fun getVariables(form: CustomForm, data: List<*>): Map<String, Variable<Any>> {
        val dataVariables = mutableListOf<Variable<Any>>()
        val dropdownVariables = mutableListOf<Variable<Any>>()
        for ((i, content) in form.contents.withIndex()) {
            val variable = when (content.type) {
                Element.Type.INPUT -> StringVariable(data[i] as String)
                Element.Type.TOGGLE -> BoolVariable(data[i] as Boolean)
                Element.Type.SLIDER -> NumberVariable(data[i] as Number)
                Element.Type.STEP_SLIDER -> NumberVariable(data[i] as Number)
                Element.Type.DROPDOWN -> NumberVariable(data[i] as Number)
                else -> StringVariable("")
            }
            dataVariables[i] = variable
            if (content is Dropdown) {
                val selected = content.options[data[i] as Int]
                dropdownVariables.add(StringVariable(selected))
            }
        }
        val variable = MapVariable(mapOf(
            "data" to ListVariable(dataVariables),
            "selected" to ListVariable(dropdownVariables),
        ))
        return mapOf("form" to variable)
    }

    override fun toString(): String {
        return when (subKey) {
            "" -> Language.get("trigger.form.string.submit", listOf(key))
            "close" -> Language.get("trigger.form.string.close", listOf(key))
            else -> Main.formManager.getForm(key)?.let { form ->
                if (form is ListForm) {
                    val button = form.getButtonById(subKey)
                    Language.get("trigger.form.string.button", listOf(key, if (button is Button) button.text else ""))
                } else {
                    key
                }
            } ?: "$key, $subKey"
        }
    }
}