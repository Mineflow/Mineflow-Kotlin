@file:Suppress("FunctionName")

package tokyo.aieuo.mineflow.utils

import com.fasterxml.jackson.databind.ObjectMapper
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun is_numeric(num: String): Boolean {
    return num.toDoubleOrNull() != null
}

fun microtime(): Double {
    return System.currentTimeMillis() / 1000.0
}

fun date(format: String): String {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern(format))
}

internal fun json_encode(obj: JsonSerializable): String {
    return ObjectMapper()
        .writerWithDefaultPrettyPrinter()
        .writeValueAsString(jsonSerializableToMap(obj))
}

internal fun jsonSerializableToMap(data: JsonSerializable): Map<Any?, Any?> {
    return jsonSerializableToMap(data.jsonSerialize())
}

internal fun jsonSerializableToMap(data: Map<*, *>): Map<Any?, Any?> {
    val map = mutableMapOf<Any?, Any?>()
    for ((k, v) in data) {
        val values = if (v is JsonSerializable) v.jsonSerialize() else v
        map[k] = when (values) {
            is List<*> -> jsonSerializableToMap(values)
            is Map<*, *> -> jsonSerializableToMap(values)
            else -> values
        }
    }
    return map
}

internal fun jsonSerializableToMap(data: List<*>): List<Any?> {
    val list = mutableListOf<Any?>()
    for (v in data) {
        val values = if (v is JsonSerializable) v.jsonSerialize() else v
        list.add(
            when (values) {
                is List<*> -> jsonSerializableToMap(values)
                is Map<*, *> -> jsonSerializableToMap(values)
                else -> values
            }
        )
    }
    return list
}