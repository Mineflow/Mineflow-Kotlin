package tokyo.aieuo.mineflow.formAPI

import cn.nukkit.Player
import tokyo.aieuo.mineflow.utils.Language

class ModalForm(title: String = "") : Form(title) {

    override val type: String = MODAL_FORM

    private var _content = ""
    private var _button1 = "@form.yes"
    private var _button2 = "@form.no"

    private var button1Click: ((Player) -> Unit)? = null
    private var button2Click: ((Player) -> Unit)? = null
    private var onReceiveWithPlayer: ((Player, Boolean) -> Unit)? = null
    private var onReceive: ((Boolean) -> Unit)? = null

    private var lastResponse: Pair<Player, Boolean?>? = null

    fun setContent(content: String) = apply {
        _content = content
    }

    fun getContent(): String {
        return _content
    }

    fun setButton1(text: String, onClick: ((Player) -> Unit)? = null) = apply {
        _button1 = text
        button1Click = onClick
    }

    fun onYes(onClick: ((Player) -> Unit)) = apply {
        button1Click = onClick
    }

    fun getButton1Text(): String {
        return _button1
    }

    fun setButton2(text: String, onClick: ((Player) -> Unit)? = null) = apply {
        _button2 = text
        button2Click = onClick
    }

    fun onNo(onClick: ((Player) -> Unit)) = apply {
        button2Click = onClick
    }

    fun setButton(index: Int, text: String, onClick: ((Player) -> Unit)? = null) = apply {
        if (index == 1) {
            setButton1(text, onClick)
        } else {
            setButton2(text, onClick)
        }
    }

    fun getButton2Text(): String {
        return _button2
    }

    fun getButtonText(index: Int): String {
        return if (index == 1) getButton1Text() else getButton2Text()
    }

    override fun jsonSerialize(): Map<String, Any?> {
        val form = mutableMapOf(
            "type" to type,
            "title" to Language.replace(_title),
            "content" to Language.replace(_content).replace("\\n", "\n"),
            "button1" to Language.replace(_button1).replace("\\n", "\n"),
            "button2" to Language.replace(_button2).replace("\\n", "\n")
        )
        return reflectErrors(form)
    }

    fun reflectErrors(form: MutableMap<String, String>): MutableMap<String, String> {
        if (messages.isNotEmpty()) {
            form["content"] = "${messages.joinToString("\n")}\n${form["content"]}"
        }
        return form
    }

    fun onReceive(callable: (Player, Boolean) -> Unit) = apply {
        onReceiveWithPlayer = callable
    }

    fun onReceive(callable: (Boolean) -> Unit) = apply {
        onReceive = callable
    }

    fun handleResponse(player: Player, data: Boolean) {
        lastResponse = player to data

        val onClick = if (data) button1Click else button2Click
        if (onClick !== null) {
            onClick(player)
            return
        }

        onReceiveWithPlayer?.let { it(player, data) }
        onReceive?.let { it(data) }
    }

    override fun resend(error: FormError) {
        val lastResponse = lastResponse ?: return
        if (!lastResponse.first.isOnline) return

        resetErrors()
            .addErrors(listOf(error))
            .show(lastResponse.first)
    }

    override fun clone(): ModalForm {
        return super.clone() as ModalForm
    }
}