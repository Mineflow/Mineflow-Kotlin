package tokyo.aieuo.mineflow.variable

abstract class ObjectVariable<out T>(val value: T, val showString: String? = null): Variable<T> {

    override val type: Int = Variable.OBJECT

    override fun toString(): String {
        if (!showString.isNullOrEmpty()) return showString

        return value.toString()
    }

    companion object {
        fun getValuesDummy(): Map<String, DummyVariable<DummyVariable.Type>> {
            return mapOf()
        }
    }
}