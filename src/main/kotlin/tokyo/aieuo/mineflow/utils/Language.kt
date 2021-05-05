package tokyo.aieuo.mineflow.utils

import tokyo.aieuo.mineflow.Main
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

object Language {

    private val messages: MutableMap<String, MutableMap<String, String>> = mutableMapOf(
        "jpn" to mutableMapOf(),
        "eng" to mutableMapOf()
    )
    var language = "eng"
    var fallbackLanguage = "eng"

    fun isAvailableLanguage(languageName: String): Boolean {
        return languageName in getAvailableLanguages()
    }

    fun getAvailableLanguages(): List<String> {
        return messages.keys.toList()
    }

    fun loadBaseMessage(language: String) {
        if (!messages.containsKey(language)) messages[language] = mutableMapOf()
        val owner = Main.instance

        val resource = owner.getResource("language/$language.ini") ?: return

        val messages = Properties()
        try {
            messages.load(InputStreamReader(resource, "UTF-8"))
        } catch (e: IOException) {
            return
        }

        for ((key, value) in messages) {
            if (key !is String || value !is String) continue

            this.messages[language]?.put(key,
                value.replace("\\n", "\n")
                    .replace("\\q", "'")
                    .replace("\\dq", "\"")
                    .let {
                        if (it.startsWith("\"") && it.endsWith("\"")) {
                            it.substring(1, it.length - 1)
                        } else if (it.startsWith("\'") && it.endsWith("\'")) {
                            it.substring(1, it.length - 1)
                        } else {
                            it
                        }
                    }
            )
        }
    }

    fun add(messages: Map<String, String>, language: String = this.language) {
        if (!this.messages.containsKey(language)) this.messages[language] = mutableMapOf()

        this.messages[language]?.putAll(messages)
    }

    fun get(key: String, replaces: List<String> = listOf(), language: String = this.language): String {
        if (exists(key, language)) {
            var message = messages[language]?.get(key) ?: return key

            replaces.withIndex().forEach { (cnt, value) -> message = message.replace("{%$cnt}", value) }
            message = message.replace("\\n", "\n").replace("\\q", "'").replace("\\dq", "\"")
            return message
        }

        if (language !== fallbackLanguage) {
            return get(key, replaces, fallbackLanguage);
        }

        return key
    }

    fun exists(key: String, language: String = this.language): Boolean {
        return messages[language]?.containsKey(key) ?: false
    }

    fun replace(text: String): String {
        val regex = Regex("@([a-zA-Z.0-9]+)")
        return text.replace(regex) { result -> result.groups[1]?.let { get(it.value) } ?: result.value }
    }

    fun getLoadErrorMessage(language: String): Array<String> {
        return when(language) {
            "jpn" -> arrayOf("言語ファイルの読み込みに失敗しました", "[${getAvailableLanguages().joinToString(", ")}]が使用できます")
            else -> arrayOf("Failed to load language file.", "Available languages are: [${getAvailableLanguages().joinToString(", ")}]")
        }
    }
}