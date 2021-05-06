package tokyo.aieuo.mineflow.formAPI

import cn.nukkit.Player
import cn.nukkit.utils.TextFormat
import tokyo.aieuo.mineflow.exception.InvalidFormValueException
import tokyo.aieuo.mineflow.formAPI.element.*
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponse
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Language

class CustomForm(title: String) : Form(title) {

    override val type: String = CUSTOM_FORM

    var contents: MutableList<Element> = mutableListOf()

    private var onReceiveWithPlayer: (CustomForm.(Player, CustomFormResponseList) -> Unit)? = null
    private var onReceive: (CustomForm.(CustomFormResponseList) -> Unit)? = null

    private var lastResponse: Pair<Player, List<Any?>>? = null

    fun addContent(content: Element, add: Boolean = true) = apply {
        if (add) contents.add(content)
    }

    fun getContent(index: Int): Element? {
        return contents.getOrNull(index)
    }

    fun addContents(contents: List<Element>) = apply {
        contents.forEach {
            this.contents.add(it)
        }
    }

    fun setContents(elements: MutableList<Element>) = apply {
        contents = elements
    }

    fun setContent(element: Element, index: Int) = apply {
        contents[index] = element
    }

    fun removeContentAt(index: Int) = apply {
        contents.removeAt(index)
    }

    override fun resetErrors() = apply {
        for (content in contents) {
            content.highlight = null
            content.extraText = ""
        }
        super.resetErrors()
    }

    override fun jsonSerialize(): Map<String, Any?> {
        val form = mutableMapOf(
            "type" to "custom_form",
            "title" to Language.replace(_title),
            "content" to contents
        )
        return reflectErrors(form)
    }

    fun reflectErrors(form: MutableMap<String, Any>): MutableMap<String, Any> {
        for ((i, content) in contents.withIndex()) {
            if (!highlights.containsKey(i)) continue

            content.highlight = TextFormat.YELLOW
        }
        if (messages.isNotEmpty() && contents.isNotEmpty()) {
            contents.getOrNull(0)?.extraText = "${messages.joinToString("\n")}\n"
        }
        return form
    }

    fun resend(
        errors: List<FormError> = listOf(),
        messages: List<String> = listOf(),
        responseOverrides: Map<Int, Any> = mapOf(),
        elementOverrides: Map<Int, Element> = mapOf()
    ) {
        val lastResponse = lastResponse ?: return
        if (!lastResponse.first.isOnline) return

        for ((i, element) in elementOverrides) {
            setContent(element, i)
        }
        setDefaultsFromResponse(lastResponse.second, responseOverrides)
            .resetErrors()
            .addMessages(messages)
            .addErrors(errors)
            .show(lastResponse.first)
    }

    override fun resend(error: FormError) = resend(listOf(error))

    fun onReceive(callable: CustomForm.(Player, CustomFormResponseList) -> Unit) = apply {
        onReceiveWithPlayer = callable
    }

    fun onReceive(callable: CustomForm.(CustomFormResponseList) -> Unit) = apply {
        onReceive = callable
    }

    fun handleResponse(player: Player, data: List<Any?>) {
        lastResponse = player to data

        val response = CustomFormResponse(this, data)
        for ((i, content) in contents.withIndex()) {
            response.currentIndex = i
            content.onFormSubmit(response, player)
        }

        val callback = response.interruptCallback
        if (callback !== null && callback()) return

        if (!response.ignoreResponse) {
            if (response.shouldResendForm() || response.hasError()) {
                resend(response.errors, listOf(), response.defaultOverrides, response.elementOverrides)
                return
            }
        }

        val newData = data.toMutableList()
        for ((i, override) in response.responseOverrides) {
            newData[i] = override
        }

        try {
            onReceiveWithPlayer?.let { it(player, CustomFormResponseList(newData)) }
            onReceive?.let { it(CustomFormResponseList(newData)) }
        } catch (e: InvalidFormValueException) {
            resend(listOf(e.errorMessage to e.index))
        }
    }

    private fun setDefaultsFromResponse(data: List<Any?>?, overwrites: Map<Int, Any>) = apply {
        if (data === null) return this

        for ((i, content) in contents.withIndex()) {
            when (content) {
                is Input -> content.default = (overwrites[i] ?: data.getOrNull(i)) as? String ?: ""
                is Slider -> content.default = (overwrites[i] ?: data.getOrNull(i)) as? Float ?: 0f
                is Dropdown -> content.default = (overwrites[i] ?: data.getOrNull(i)) as? Int ?: 0
            }
        }
    }

    override fun clone(): CustomForm {
        val form = super.clone() as CustomForm

        val elements = mutableListOf<Element>()
        for (content in contents) {
            elements.add((content as ElementBase).clone())
        }
        form.contents = elements

        return form
    }
}