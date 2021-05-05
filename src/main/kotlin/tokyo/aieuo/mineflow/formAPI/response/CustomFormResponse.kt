package tokyo.aieuo.mineflow.formAPI.response

import tokyo.aieuo.mineflow.formAPI.CustomForm
import tokyo.aieuo.mineflow.formAPI.element.Element

class CustomFormResponse(val customForm: CustomForm, response: List<*>): FormResponse<List<*>>(response) {

    val responseOverrides = mutableMapOf<Int, Any>()
    val defaultOverrides = mutableMapOf<Int, Any>()
    val elementOverrides = mutableMapOf<Int, Element>()

    var ignoreResponse = false
    var resend = false

    var interruptCallback: (() -> Boolean)? = null


    fun getInputResponse(): String {
        return response[currentIndex] as String
    }

    fun getDropdownResponse(): Int {
        return response[currentIndex] as Int
    }

    fun getToggleResponse(): Boolean {
        return response[currentIndex] as Boolean
    }

    fun overrideResponse(response: Any) {
        responseOverrides[currentIndex] = response
    }

    fun overrideElement(element: Element, default: Any? = null) {
        elementOverrides[currentIndex] = element
        if (default !== null) defaultOverrides[currentIndex] = default
    }

    fun shouldResendForm(): Boolean {
        return resend
    }
}