package tokyo.aieuo.mineflow.formAPI

import cn.nukkit.Player
import tokyo.aieuo.mineflow.formAPI.element.Button
import tokyo.aieuo.mineflow.utils.Language

class ListForm(title: String = ""): Form(title) {

    override val type: String = LIST_FORM

    private var _content = "@form.selectButton"
    var buttons: MutableList<Button> = mutableListOf()

    private var onReceiveWithPlayer: ((Player, Int) -> Unit)? = null
    private var onReceive: ((Int) -> Unit)? = null

    private var lastResponse: Pair<Player, Int?>? = null

    fun setContent(content: String) = apply {
        _content = content
    }

    fun getContent(): String {
        return _content
    }

	fun appendContent(content: String, newLine: Boolean = true) = apply {
		_content += (if (newLine) "\n" else "") + content
    }

    fun addButton(button: Button) = apply {
        buttons.add(button)
    }

    fun addButtons(buttons: List<Button>) = apply {
        for (button in buttons) {
            this.buttons.add(button)
        }
    }

    fun addButtons(vararg buttons: Button) = apply {
        for (button in buttons) {
            this.buttons.add(button)
        }
    }

    fun setButtons(buttons: MutableList<Button>) = apply {
        this.buttons = buttons
    }

    fun <T> addButtonsEach(inputs: List<T>, convert: (T) -> Button) = apply {
        for (input in inputs) {
            addButton(convert(input))
        }
    }

    fun <T> addButtonsEach(inputs: List<T>, convert: (T, Int) -> Button) = apply {
        for ((i, input) in inputs.withIndex()) {
            addButton(convert(input, i))
        }
    }

    fun <K, V> addButtonsEach(inputs: Map<K, V>, convert: (V, K) -> Button) = apply {
        for ((i, input) in inputs) {
            addButton(convert(input, i))
        }
    }

    fun removeButton(index: Int) = apply {
        buttons.removeAt(index)
    }

    fun getButton(index: Int): Button? {
        return buttons.getOrNull(index)
    }

    fun getButtonById(id: String): Button? {
        for (button in buttons) {
            if (button.getUUID() == id) return button
        }
        return null
    }

    override fun jsonSerialize(): Map<String, Any?> {
        val form = mutableMapOf(
            "type" to "form",
            "title" to Language.replace(_title),
            "content" to Language.replace(_content).replace("\\n", "\n"),
            "buttons" to buttons
        )
        return reflectErrors(form)
    }

    fun reflectErrors(form: MutableMap<String, Any>): MutableMap<String, Any> {
        if (messages.isNotEmpty()) {
            form["content"] = "${messages.joinToString("\n")}\n${form["content"]}"
        }
        return form
    }

    fun onReceive(callable: (Player, Int) -> Unit) = apply {
        onReceiveWithPlayer = callable
    }

    fun onReceive(callable: (Int) -> Unit) = apply {
        onReceive = callable
    }

    fun handleResponse(player: Player, data: Int) {
        lastResponse = player to data

        val button = getButton(data)
        if (button !== null && button.onClick !== null) {
            button.onClick?.let { it(player) }
            if (button.skipIfCallOnClick) return
        }

        onReceiveWithPlayer?.let { it(player, data) }
        onReceive?.let { it(data) }
    }

    fun resend(errors: List<FormError> = listOf(), messages: List<String> = listOf()) {
        val lastResponse = lastResponse ?: return
        if (!lastResponse.first.isOnline) return

        resetErrors()
            .addMessages(messages)
            .addErrors(errors)
            .show(lastResponse.first)
    }

    override fun resend(error: FormError) = resend(listOf(error))

    override fun clone(): ListForm {
        val form = super.clone() as ListForm
        val buttons = mutableListOf<Button>()
        for (button in buttons) {
            buttons.add(button.clone().uuid(button.getUUID()))
        }
        form.buttons = buttons
        return form
    }
}