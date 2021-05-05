package tokyo.aieuo.mineflow.variable

import cn.nukkit.utils.Config
import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.exception.UndefinedMineflowPropertyException
import tokyo.aieuo.mineflow.exception.UndefinedMineflowVariableException
import tokyo.aieuo.mineflow.exception.UnsupportedCalculationException
import tokyo.aieuo.mineflow.utils.JsonSerializable
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.utils.is_numeric
import tokyo.aieuo.mineflow.utils.jsonSerializableToMap

class VariableHelper(val file: Config) {

    private val variables: MutableMap<String, Variable<Any>> = mutableMapOf()

    init {
        @Suppress("UNCHECKED_CAST")
        for ((name, data) in file.all) {
            val variable = Variable.fromArray(data as Map<String, Any>)

            if (variable === null) {
                Main.instance.logger.warning(Language.get("variable.load.failed", listOf(name)))
                continue
            }

            variables[name] = variable
        }
    }

    fun exists(name: String): Boolean {
        return variables.containsKey(name)
    }

    fun get(name: String): Variable<Any>? {
        return variables[name]
    }

    fun getNested(_name: String): Variable<Any>? {
        val names = ArrayDeque(_name.split("."))
        val name = names.removeFirstOrNull() ?: _name
        if (!exists(name)) return null

        var variable = get(name)
        for (name1 in names) {
            if (variable !is Variable) return null
            variable = variable.getValueFromIndex(name1)
        }
        return variable
    }

    fun add(name: String, variable: Variable<Any>) {
        variables[name] = variable
    }

    fun delete(name: String) {
        variables.remove(name)
    }

    fun saveAll() {
        for ((name, variable) in variables) {
            if (variable !is JsonSerializable) continue
            file.set(name, jsonSerializableToMap(variable))
        }
        file.save()
    }

    fun findVariables(string: String): List<String> {
        val variables = mutableListOf<String>()
        val firstBracket = string.indexOf("{")
        val lastBracket = string.lastIndexOf("}")
        if (firstBracket == -1 || lastBracket == -1) return listOf()

        var index = firstBracket
        var startIndex = -1
        var foundBracket = false
        for (i in firstBracket..lastBracket) {
            when (string[i]) {
                '{' -> {
                    startIndex = index
                    foundBracket = true
                }
                '}' -> {
                    if (foundBracket) {
                        variables.add(string.substring(startIndex + 1, index))
                    }
                    foundBracket = false
                }
            }
            index ++
        }
        return variables
    }

    fun replaceVariables(_string: String, variables: Map<String, Variable<Any>> = mapOf(), global: Boolean = true): String {
        var string = _string

        for (i in 0..10) {
            for (value in findVariables(string)) {
                string = replaceVariable(string, value, variables, global)
            }
            if (!("{" in string && "}" in string)) break
        }
        return string
    }

    fun replaceVariable(string: String, replace: String, variables: Map<String, Variable<Any>> = mapOf(), global: Boolean = true): String {
        if (!string.contains("{$replace}")) return string

        val tokens = lexer(replace)

        @Suppress("UNCHECKED_CAST")
        val result = when (val ast = parse(tokens)) {
            is String -> mustGetVariableNested(ast, variables, global).toString()
            is Variable<*> -> ast.toString()
            is Map<*, *> -> run(ast as Map<String, Any>, variables, global).toString()
            else -> ""
        }

        return string.replace("{$replace}", result)
    }

    fun lexer(source: String): ArrayDeque<String> {
        val tokens = mutableListOf<String>()
        var token = ""
        var brackets = 0

        for (char in source.replace(Regex("""\[(.*?)]"""), ".$1")) {
            when (char) {
                '+', '-', '*', '/', '(', ')' -> {
                    tokens.add(token.trim())
                    tokens.add(char.toString())
                    token = ""

                    if (char == '(') {
                        brackets ++
                    } else if (char == ')') {
                        brackets --
                    }
                }
                ',' -> {
                    if (brackets > 0) {
                        tokens.add(token.trim())
                        tokens.add(char.toString())
                        token = ""
                    } else {
                        token += char
                    }
                }
                else -> token += char
            }
        }
        if (token.isNotBlank()) tokens.add(token.trim())
        return ArrayDeque(tokens.filterNot { it.isBlank() })
    }

    fun parse(tokens: ArrayDeque<String>, priority: Int = 0): Any {
        val rules = listOf(
            1 to listOf(","),
            0 to listOf("+", "-"), // 1 + 2, 1 - 2
            0 to listOf("*", "/"), // 1 * 2, 1 / 2
            2 to listOf("+", "-"), // +1, -1
            3 to listOf("("), // method aiueo(1)
            4 to listOf("("), // (1 + 2)
        )

        if (priority >= rules.size) {
            val value = tokens.removeFirst()
            return if (is_numeric(value)) NumberVariable(value.toDouble()) else value
        }

        val type = rules[priority].component1()
        val ops = rules[priority].component2()

        if (type == 1) {
            val left = parse(tokens, priority + 1)
            val list = mutableListOf(left)
            while (tokens.size > 0 && ops.contains(tokens[0])) {
                tokens.removeFirst()
                list.add(parse(tokens, priority + 1))
            }
            return if (list.size > 1) list else left
        }

        if ((type == 2 || type == 4) && !ops.contains(tokens[0])) {
            return parse(tokens, priority + 1)
        }

        if (type == 2) {
            return listOf("left" to 0, "op" to tokens.removeFirst(), "right" to parse(tokens, priority + 1))
        }
        if (type == 4) {
            tokens.removeFirst() // (
            val right = parse(tokens, 0)
            tokens.removeFirst() // )
            return right
        }

        if (type == 3) {
            var left = parse(tokens, priority + 1)
            while (tokens.isNotEmpty() && ops.contains(tokens[0])) {
                tokens.removeFirst() // (
                val right = if (tokens[0] == ")") "" else parse(tokens, 0)
                tokens.removeFirst() // )
                val tmp = left
                left = mapOf("left" to tmp, "op" to "()", "right" to right)
            }
            return left
        }

        var left = parse(tokens, priority + 1)
        while (tokens.isNotEmpty() && ops.contains(tokens[0])) {
            val tmp = left
            left = mapOf("left" to tmp, "op" to tokens.removeFirst(), "right" to parse(tokens, priority + 1))
        }
        return left
    }

    @Suppress("UNCHECKED_CAST")
    fun run(ast: Map<String, Any>, variables: Map<String, Variable<Any>> = mapOf(), global: Boolean = false): Variable<Any> {
        var left = if (ast["left"] is Map<*, *>) run(ast["left"] as Map<String, Any>, variables, global) else ast["left"]
        var right = if (ast["right"] is Map<*, *>) run(ast["right"] as Map<String, Any>, variables, global) else ast["right"]
        val op = ast["op"]

        if (op == "()") {
            throw UnsupportedCalculationException()
        }

        if (left is String) {
            left = mustGetVariableNested(left, variables, global)
        }
        if (right is String) {
            right = mustGetVariableNested(right, variables, global)
        }
        left = left as Variable<Any>
        right = right as Variable<Any>

        return when (op) {
            "+" -> left + right
            "-" -> left - right
            "*" -> left * right
            "/" -> left / right
            else -> throw UnsupportedCalculationException()
        }
    }

    fun mustGetVariableNested(_name: String, variables: Map<String, Variable<Any>> = mapOf(), global: Boolean = false): Variable<Any> {
        val names = ArrayDeque(_name.split("."))
        val name = names.removeFirstOrNull() ?: _name
        if (!variables.containsKey(name) && !exists(name)) throw UndefinedMineflowVariableException(name)

        var variable = variables[name] ?: (if (global) get(name) else null)
        if (variable === null) throw UndefinedMineflowVariableException(name)

        var tmp = name
        for (name1 in names) {
            if (variable !is Variable) throw UndefinedMineflowPropertyException(tmp, name1)

            variable = variable.getValueFromIndex(name1)
            tmp += ".$name1"
        }

        if (variable === null) throw UndefinedMineflowPropertyException(tmp, "")
        return variable
    }

    fun isVariableString(variable: String): Boolean {
        return Regex("""^\{[^{}\[\].]+}$""").containsMatchIn(variable)
    }

    fun containsVariable(variable: String): Boolean {
        return Regex("""\{.+}""").containsMatchIn(variable)
    }

    fun getType(string: String): Int {
        return when {
            string.startsWith("(str)") -> Variable.STRING
            string.startsWith("(num)") -> Variable.NUMBER
            is_numeric(string) -> Variable.NUMBER
            else -> Variable.STRING
        }
    }

    fun currentType(value: String): Any {
        return when {
            value.startsWith("(str)") -> value.substring(5)
            value.startsWith("(num)") -> value.substring(5).let {
                if (containsVariable(value)) it else it.toFloat()
            }
            is_numeric(value) -> value.toFloat()
            else -> value
        }
    }

    fun toVariableArray(data: Map<*, *>): Map<String, Variable<Any>> {
        val result = HashMap<String, Variable<Any>>()
        for ((key, value) in data) {
            if (key !is String) continue

            result[key] = when {
                value is List<*> -> ListVariable(toVariableArray(value))
                value is Map<*, *> -> MapVariable(toVariableArray(value))
                value is String && is_numeric(value) -> NumberVariable(value.toFloat())
                else -> StringVariable(value.toString())
            }
        }
        return result
    }

    fun toVariableArray(data: List<*>): List<Variable<Any>> {
        val result = mutableListOf<Variable<Any>>()
        for (value in data) {
            result.add(when {
                value is List<*> -> ListVariable(toVariableArray(value))
                value is Map<*, *> -> MapVariable(toVariableArray(value))
                value is String && is_numeric(value) -> NumberVariable(value.toFloat())
                else -> StringVariable(value.toString())
            })
        }
        return result
    }
}