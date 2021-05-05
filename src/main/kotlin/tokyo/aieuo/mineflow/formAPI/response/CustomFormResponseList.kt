package tokyo.aieuo.mineflow.formAPI.response

class CustomFormResponseList(values: List<Any?>): ArrayList<Any?>(values) {

    fun getString(index: Int): String {
        return when(val value = get(index)) {
            is String -> value
            else -> value.toString()
        }
    }

    fun getStringOrNull(index: Int): String? {
        return when(val value = getOrNull(index) ?: return null) {
            is String -> value
            else -> value.toString()
        }
    }

    fun getInt(index: Int): Int {
        return when(val value = get(index)) {
            is Int -> value
            is Float -> value.toInt()
            is Double -> value.toInt()
            is String -> value.toInt()
            else -> value as Int
        }
    }

    fun getIntOrNull(index: Int): Int? {
        return when(val value = getOrNull(index) ?: return null) {
            is Int -> value
            is Float -> value.toInt()
            is Double -> value.toInt()
            is String -> value.toIntOrNull()
            else -> value as? Int
        }
    }

    fun getFloat(index: Int): Float {
        return when(val value = get(index)) {
            is Int -> value.toFloat()
            is Float -> value
            is Double -> value.toFloat()
            is String -> value.toFloat()
            else -> value as Float
        }
    }

    fun getFloatOrNull(index: Int): Float? {
        return when(val value = getOrNull(index) ?: return null) {
            is Int -> value.toFloat()
            is Float -> value
            is Double -> value.toFloat()
            is String -> value.toFloatOrNull()
            else -> value as? Float
        }
    }

    fun getDouble(index: Int): Double {
        return when(val value = get(index)) {
            is Int -> value.toDouble()
            is Float -> value.toDouble()
            is Double -> value
            is String -> value.toDouble()
            else -> value as Double
        }
    }

    fun getDoubleOrNull(index: Int): Double? {
        return when(val value = getOrNull(index) ?: return null) {
            is Int -> value.toDouble()
            is Float -> value.toDouble()
            is Double -> value
            is String -> value.toDoubleOrNull()
            else -> value as? Double
        }
    }

    fun getBoolean(index: Int): Boolean {
        return when(val value = get(index)) {
            is Boolean -> value
            is String -> when (value) {
                "yes", "on", "true" -> true
                else -> false
            }
            else -> value as Boolean
        }
    }

    fun getBooleanOrNull(index: Int): Boolean? {
        return when(val value = getOrNull(index) ?: return null) {
            is Boolean -> value
            is String -> when (value) {
                "yes", "on", "true" -> true
                else -> false
            }
            else -> value as? Boolean
        }
    }

}