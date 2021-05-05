package tokyo.aieuo.mineflow.variable.obj

import cn.nukkit.level.Level
import tokyo.aieuo.mineflow.variable.*

class WorldObjectVariable<T: Level>(value: T, showString: String? = null): ObjectVariable<T>(value, showString) {

    override fun getValueFromIndex(index: String): Variable<Any>? {
        return when (index) {
            "name" -> StringVariable(value.name)
            "folderName" -> StringVariable(value.folderName)
            "id" -> NumberVariable(value.id)
            else -> null
        }
    }


    companion object {
        fun getValuesDummy(): Map<String, DummyVariable<DummyVariable.Type>> {
            return mapOf(
                "name" to DummyVariable(DummyVariable.Type.STRING),
                "folderName" to DummyVariable(DummyVariable.Type.STRING),
                "id" to DummyVariable(DummyVariable.Type.NUMBER),
            )
        }
    }
}