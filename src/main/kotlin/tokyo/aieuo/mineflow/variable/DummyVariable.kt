package tokyo.aieuo.mineflow.variable

import tokyo.aieuo.mineflow.variable.obj.*

class DummyVariable<T: DummyVariable.Type>(val valueType: T, val description: String = "") : Variable<T> {

    override val type = Variable.DUMMY

    enum class Type {
        UNKNOWN,
        STRING,
        NUMBER,
        BOOLEAN,
        LIST,
        MAP,
        BLOCK,
        CONFIG,
        ENTITY,
        EVENT,
        HUMAN,
        ITEM,
        WORLD,
        LOCATION,
        PLAYER,
        POSITION,
        VECTOR3,
        SCOREBOARD;
    }

    constructor(valueType: T, description: T): this(valueType, description.name.toLowerCase())

    override fun toString(): String {
        return "dummy"
    }

    override fun getValueFromIndex(index: String): Variable<Any>? {
        return getObjectValuesDummy()[index]
    }

    fun getObjectValuesDummy(): Map<String, DummyVariable<Type>> {
        return when (valueType) {
            Type.BLOCK -> BlockObjectVariable.getValuesDummy()
            Type.CONFIG -> ConfigObjectVariable.getValuesDummy()
            Type.ENTITY -> EntityObjectVariable.getValuesDummy()
            Type.EVENT -> EventObjectVariable.getValuesDummy()
            Type.HUMAN -> HumanObjectVariable.getValuesDummy()
            Type.ITEM -> ItemObjectVariable.getValuesDummy()
            Type.WORLD -> WorldObjectVariable.getValuesDummy()
            Type.LOCATION -> LocationObjectVariable.getValuesDummy()
            Type.PLAYER -> PlayerObjectVariable.getValuesDummy()
            Type.POSITION -> PositionObjectVariable.getValuesDummy()
            Type.VECTOR3 -> Vector3ObjectVariable.getValuesDummy()
            Type.SCOREBOARD -> ScoreboardObjectVariable.getValuesDummy()
            else -> mapOf()
        }
    }

    fun isObjectVariableType(): Boolean {
        return valueType in listOf(
            Type.BLOCK,
            Type.CONFIG,
            Type.ENTITY,
            Type.EVENT,
            Type.HUMAN,
            Type.ITEM,
            Type.WORLD,
            Type.LOCATION,
            Type.PLAYER,
            Type.POSITION,
            Type.VECTOR3,
            Type.SCOREBOARD,
        )
    }
}