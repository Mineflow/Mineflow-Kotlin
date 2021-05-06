package tokyo.aieuo.mineflow.flowItem.action.item

import cn.nukkit.Server
import cn.nukkit.inventory.ShapedRecipe
import cn.nukkit.item.Item
import tokyo.aieuo.mineflow.exception.InvalidFlowValueException
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.ItemFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ItemVariableDropdown
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language

class RegisterCraftingRecipe(
    i1: String = "",
    i2: String = "",
    i3: String = "",
    i4: String = "",
    i5: String = "",
    i6: String = "",
    i7: String = "",
    i8: String = "",
    i9: String = "",
    o: String = ""
) : FlowItem(), ItemFlowItem {

    override val id = FlowItemIds.REGISTER_SHAPED_RECIPE

    override val nameTranslationKey = "action.registerRecipe.name"
    override val detailTranslationKey = "action.registerRecipe.detail"
    override val detailDefaultReplaces = listOf("inputs", "outputs")

    override val category = Category.ITEM

    override var itemVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setInputItemVariableNames(listOf(i1, i2, i3, i4, i5, i6, i7, i8, i9))
        setItemVariableName(o, "output")
    }

    override fun isDataValid(): Boolean {
        return getItemVariableName("output") != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()

        val shape = mutableListOf("", "", "")
        val ingredients = mutableMapOf<Char, String>()
        val keys = listOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I')
        var index = -1
        val items = mutableMapOf<String, Int>()
        for (i in 0..8) {
            val input = getItemVariableName("input$i")
            val key: Char
            if (input != "") {
                if (items.containsKey(input)) {
                    key = keys[items[input] ?: continue]
                } else {
                    index++
                    items[input] = index
                    key = keys[index]
                    ingredients[key] = input
                }
            } else {
                key = ' '
            }
            shape[i / 3] += key.toString()
        }
        trimShape(shape)

        val details = mutableListOf("---${Language.get(detailTranslationKey)}---")
        details.add(Language.get("action.registerRecipe.shape"))
        for (line in shape) {
            details.add("- |$line|")
        }
        details.add(Language.get("action.registerRecipe.ingredients"))
        for ((key, ingredient) in ingredients) {
            details.add("- $key = $ingredient")
        }
        details.add(Language.get("action.registerRecipe.results"))
        details.add("- ${getItemVariableName("output")}")
        details.add("------------------------")
        return details.joinToString("\n")
    }

    fun setInputItemVariableNames(items: List<String>) {
        for (i in 0..8) {
            setItemVariableName(items[i], "input$i")
        }
    }

    fun getInputItemVariableNames(): List<String> {
        val items = mutableListOf<String>()
        for (i in 0..8) {
            items.add(getItemVariableName("input$i"))
        }
        return items
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val output = getItem(source, "output")
        val shape = mutableListOf("", "", "")
        val ingredients = mutableMapOf<Char, Item>()
        val keys = listOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I')
        var index = -1
        val items = mutableMapOf<String, Int>()
        for (i in 0..8) {
            var key: Char
            try {
                val input = getItem(source, "input$i")
                val itemId = "${input.id}:${input.damage}"
                if (items.containsKey(itemId)) {
                    key = keys[items[itemId] ?: continue]
                } else {
                    index++
                    items[itemId] = index
                    key = keys[index]
                    ingredients[key] = input
                }
            } catch (e: InvalidFlowValueException) {
                key = ' '
            }
            shape[i / 3] += key.toString()
        }

        trimShape(shape)
        if (shape.isEmpty()) {
            throw InvalidFlowValueException(Language.get("action.registerRecipe.recipe.empty"))
        }

        val recipe = ShapedRecipe(output, shape.toTypedArray(), ingredients, listOf())
        Server.getInstance().craftingManager.registerShapedRecipe(recipe)
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    fun trimShape(shape: MutableList<String>) {
        val col = mutableListOf("", "", "")
        for (i in 0..2) {
            for (j in 0..2) {
                col[i] = col[i] + shape[j][i]
            }
        }

        var colStart = 0
        var colEnd = 2
        if (col[0] == "   ") colStart++
        if (col[2] == "   ") colEnd--
        if (col[0] == "   " && col[2] != "   " && col[1] == "   ") colStart++
        if (col[2] == "   " && col[0] != "   " && col[1] == "   ") colEnd--
        if (col[0] == "   " && col[1] == "   " && col[2] == "   ") {
            shape.clear()
            return
        }

        for (i in 2 downTo 0) {
            val line = shape[i].substring(colStart, colEnd - colStart + 1)
            shape[i] = line
            if (line.trim() == "") shape.removeAt(i)
        }
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            ItemVariableDropdown(
                variables,
                getItemVariableName("input0"),
                "@action.registerRecipe.ingredients A",
                true
            ),
            ItemVariableDropdown(
                variables,
                getItemVariableName("input1"),
                "@action.registerRecipe.ingredients B",
                true
            ),
            ItemVariableDropdown(
                variables,
                getItemVariableName("input2"),
                "@action.registerRecipe.ingredients C",
                true
            ),
            ItemVariableDropdown(
                variables,
                getItemVariableName("input3"),
                "@action.registerRecipe.ingredients D",
                true
            ),
            ItemVariableDropdown(
                variables,
                getItemVariableName("input4"),
                "@action.registerRecipe.ingredients E",
                true
            ),
            ItemVariableDropdown(
                variables,
                getItemVariableName("input5"),
                "@action.registerRecipe.ingredients F",
                true
            ),
            ItemVariableDropdown(
                variables,
                getItemVariableName("input6"),
                "@action.registerRecipe.ingredients G",
                true
            ),
            ItemVariableDropdown(
                variables,
                getItemVariableName("input7"),
                "@action.registerRecipe.ingredients H",
                true
            ),
            ItemVariableDropdown(
                variables,
                getItemVariableName("input8"),
                "@action.registerRecipe.ingredients I",
                true
            ),
            ItemVariableDropdown(variables, getItemVariableName("output"), "@action.registerRecipe.results RESULT"),
        )
    }

    override fun parseFromFormData(data: CustomFormResponseList): List<Any?> {
        val items = ArrayDeque(data)
        val result = items.removeLast()
        return listOf(items, result)
    }

    @Suppress("UNCHECKED_CAST")
    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setInputItemVariableNames(contents[0] as List<String>)
        setItemVariableName(contents.getString(1), "output")
    }

    override fun serializeContents(): List<Any> {
        return listOf(getInputItemVariableNames(), getItemVariableName("output"))
    }

    override fun clone(): RegisterCraftingRecipe {
        val item = super.clone() as RegisterCraftingRecipe
        item.itemVariableNames = itemVariableNames.toMutableMap()
        return item
    }
}