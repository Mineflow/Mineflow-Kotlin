package tokyo.aieuo.mineflow.utils

interface JsonSerializable {
    fun jsonSerialize(): Map<String, Any?>
}