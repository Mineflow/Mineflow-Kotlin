package tokyo.aieuo.mineflow.formAPI.response

import tokyo.aieuo.mineflow.formAPI.FormError

open class FormResponse<T>(val response: T) {

    val errors = mutableListOf<FormError>()

    var currentIndex = 0

    fun addError(error: String) {
        errors.add(error to currentIndex)
    }

    fun hasError(): Boolean {
        return errors.size > 0
    }
}