package tokyo.aieuo.mineflow.formAPI

import cn.nukkit.Player
import cn.nukkit.utils.TextFormat
import tokyo.aieuo.mineflow.formAPI.FormAPI.sendForm
import tokyo.aieuo.mineflow.formAPI.element.*
import tokyo.aieuo.mineflow.formAPI.element.mineflow.CommandButton
import tokyo.aieuo.mineflow.formAPI.utils.ButtonImage
import tokyo.aieuo.mineflow.utils.JsonSerializable
import tokyo.aieuo.mineflow.utils.Language

abstract class Form(title: String) : JsonSerializable, Cloneable {

    open val type: String = LIST_FORM

    protected var _title: String = title
    protected var _name: String? = null

    private var onClose: ((Player) -> Unit)? = null

    protected val messages: MutableSet<String> = mutableSetOf()
    protected val highlights: MutableMap<Int, TextFormat> = mutableMapOf()

    fun setTitle(title: String) = apply {
        _title = title
    }

    fun getTitle(): String {
        return _title
    }

    fun setName(name: String) = apply {
        _name = name
    }

    fun getName(): String {
        return _name ?: _title
    }

    fun <T> forEach(inputs: List<T>, func: (Form, T, Int) -> Unit) = apply {
        for ((i, input) in inputs.withIndex()) {
            func(this, input, i)
        }
    }

    fun onClose(callable: (Player) -> Unit) = apply {
        onClose = callable
    }

    fun addError(error: String, index: Int) = apply {
        messages.add(TextFormat.RED.toString() + Language.replace(error) + TextFormat.WHITE.toString())
        highlights[index] = TextFormat.YELLOW
    }

    fun addErrors(errors: List<FormError>) = apply {
        for ((error, index) in errors) {
            addError(error, index)
        }
    }

    fun addMessage(message: String) = apply {
        messages.add(TextFormat.AQUA.toString() + Language.replace(message) + TextFormat.WHITE.toString())
    }

    fun addMessages(messages: List<String>) = apply {
        for (message in messages) {
            addMessage(message)
        }
    }

    open fun resetErrors() = apply {
        messages.clear()
        highlights.clear()
    }

    fun show(player: Player) = apply {
        player.sendForm(this)
    }

    fun handleOnClose(player: Player) {
        (onClose ?: return)(player)
    }

    companion object {

        const val MODAL_FORM = "modal"
        const val LIST_FORM = "form"
        const val CUSTOM_FORM = "custom_form"

        @Suppress("UNCHECKED_CAST")
        fun createFromArray(data: Map<String, Any>, name: String = ""): Form? {
            if (!data.contains("type") || !data.contains("title")) return null

            when (data["type"] as String) {
                MODAL_FORM -> {
                    if (!data.containsKey("content") || !data.containsKey("button1") || !data.containsKey("button2")) return null

                    return ModalForm(data["title"] as String)
                        .setContent(data["content"] as String)
                        .setButton1(data["button1"] as String)
                        .setButton2(data["button2"] as String)
                        .setName(name)
                }
                LIST_FORM -> {
                    if (!data.containsKey("content") || !data.containsKey("buttons")) return null

                    return ListForm(data["title"] as String)
                        .setContent(data["content"] as String).apply {
                            for (buttonData in (data["buttons"] as List<Map<String, Any>>)) {
                                if (!buttonData.containsKey("text")) return null
                                val text = buttonData["text"] as String

                                val button = if (buttonData.containsKey("mineflow")) {
                                    val mfData = buttonData["mineflow"] as? Map<String, Any>
                                    if (mfData === null || !mfData.containsKey("command")) {
                                        Button(text)
                                    } else {
                                        CommandButton(mfData["command"] as String, text)
                                    }
                                } else {
                                    Button(buttonData["text"] as String)
                                }
                                if (buttonData["image"] !== null) {
                                    val imageData = buttonData["image"] as Map<String, String>
                                    button.image = ButtonImage(imageData["data"] ?: "", imageData["type"] ?: "")
                                }
                                addButton(button.uuid((buttonData["id"] ?: "") as String))
                            }
                        }.setName(name)
                }
                CUSTOM_FORM -> {
                    if (!data.contains("content")) return null

                    val form = CustomForm(data["title"] as String)
                    for (content in data["content"] as List<Map<String, Any>>) {
                        if (!content.containsKey("type") || !content.containsKey("text")) return null

                        val text = content["text"] as String
                        val element = when (content["type"] as String) {
                            Element.Type.LABEL.typeName -> Label(text)
                            Element.Type.TOGGLE.typeName -> {
                                val default = (content["default"] ?: false) as Boolean

                                if (content.containsKey("mineflow")) {
                                    val mfData = content["mineflow"] as? Map<String, Any>
                                    if (mfData !== null && mfData["type"] == "cancelToggle") {
                                        CancelToggle(null, text, default)
                                    } else {
                                        Toggle(text, default)
                                    }
                                } else {
                                    Toggle(text, default)
                                }
                            }
                            Element.Type.INPUT.typeName -> {
                                val placeholder = content["placeholder"] as? String ?: ""
                                val default = content["default"] as? String ?: ""
                                val mfData = content["mineflow"] as? Map<String, Any> ?: mapOf()
                                val required = mfData["required"] as? Boolean ?: false
                                if (mfData["type"] == "number") {
                                    val min = mfData["min"] as? Double
                                    val max = mfData["max"] as? Double
                                    val excludes = mfData["excludes"] as? List<Double> ?: listOf()
                                    NumberInput(text, placeholder, default, required, min, max, excludes)
                                } else {
                                    Input(text, placeholder, default, required)
                                }
                            }
                            Element.Type.SLIDER.typeName -> {
                                if (!content.containsKey("min") || !content.containsKey("max")) return null
                                Slider(
                                    text,
                                    (content["min"] as Double).toFloat(),
                                    (content["max"] as Double).toFloat(),
                                    (content["step"] as? Double)?.toFloat() ?: 1f,
                                    content["default"]?.let { (it as Double).toFloat() }
                                )
                            }
                            Element.Type.STEP_SLIDER.typeName -> {
                                if (!content.containsKey("steps")) return null
                                StepSlider(
                                    text,
                                    (content["steps"] as List<String>).toMutableList(),
                                    (content["default"] as? Double)?.toInt() ?: 0
                                )
                            }
                            Element.Type.DROPDOWN.typeName -> {
                                if (!content.containsKey("options")) return null
                                Dropdown(
                                    text,
                                    content["options"] as List<String>,
                                    (content["default"] as? Double)?.toInt() ?: 0
                                )
                            }
                            else -> return null
                        }
                        form.addContent(element)
                    }
                    return form
                }
                else -> return null
            }
        }
    }

    abstract fun resend(error: FormError)

    public override fun clone(): Form {
        return super.clone() as Form
    }
}