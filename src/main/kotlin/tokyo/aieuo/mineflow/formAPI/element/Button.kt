package tokyo.aieuo.mineflow.formAPI.element

import cn.nukkit.Player
import tokyo.aieuo.mineflow.formAPI.utils.ButtonImage
import tokyo.aieuo.mineflow.utils.JsonSerializable
import tokyo.aieuo.mineflow.utils.Language
import java.util.*

open class Button(text: String, var onClick: ((Player) -> Unit)? = null, var image: ButtonImage? = null): JsonSerializable, Cloneable {

    companion object {
        const val TYPE_NORMAL = "button"
        const val TYPE_COMMAND = "commandButton"
    }

    protected open val type: String = TYPE_NORMAL

    var text: String = text.replace("\\n", "\n")
        set(value) {
            field = value.replace("\\n", "\n")
        }

    protected val extraText: String = ""
    private var _uuid = "" // by lazy?

    /** @var bool */
    open val skipIfCallOnClick: Boolean = true // TODO: 名前...

    constructor(text: String, onClick: ((Player) -> Unit)): this(text, onClick, null)

    fun uuid(id: String) = apply {
        _uuid = id
    }

    fun getUUID(): String {
        if (_uuid.isEmpty()) _uuid = UUID.randomUUID().toString()
        return _uuid
    }

    override fun toString(): String {
        return Language.get("form.form.formMenu.list.button", listOf(text))
    }

    override fun jsonSerialize(): Map<String, Any?> {
        return mapOf(
            "text" to Language.replace(text),
            "id" to getUUID(),
            "image" to image,
        )
    }

    public override fun clone(): Button {
        return super.clone() as Button
    }
}