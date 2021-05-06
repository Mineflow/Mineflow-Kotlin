package tokyo.aieuo.mineflow.formAPI.utils

import tokyo.aieuo.mineflow.utils.JsonSerializable

class ButtonImage(var image: String, var type: String = TYPE_PATH) : JsonSerializable {

    companion object {
        const val TYPE_PATH = "path"
        const val TYPE_URL = "url"
    }

    override fun jsonSerialize(): Map<String, Any?> {
        return mapOf(
            "type" to type,
            "data" to image,
        )
    }
}