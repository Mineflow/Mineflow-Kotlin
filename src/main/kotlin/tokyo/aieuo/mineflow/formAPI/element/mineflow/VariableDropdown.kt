package tokyo.aieuo.mineflow.formAPI.element.mineflow

import cn.nukkit.Player
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemContainer
import tokyo.aieuo.mineflow.flowItem.FlowItemFactory
import tokyo.aieuo.mineflow.formAPI.CustomForm
import tokyo.aieuo.mineflow.formAPI.ListForm
import tokyo.aieuo.mineflow.formAPI.element.Button
import tokyo.aieuo.mineflow.formAPI.element.Dropdown
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponse
import tokyo.aieuo.mineflow.recipe.Recipe
import tokyo.aieuo.mineflow.ui.FlowItemForm
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.utils.Session
import tokyo.aieuo.mineflow.variable.DummyVariable
import java.util.*
import kotlin.collections.ArrayDeque

abstract class VariableDropdown(
    text: String,
    variables: DummyVariableMap,
    val variableTypes: List<DummyVariable.Type> = listOf(),
    var defaultText: String = "",
    val optional: Boolean = false
) : Dropdown(text) {

    protected open val variableType = DummyVariable.Type.UNKNOWN
    protected open val actions = listOf<String>()

    private var variableNames = listOf<String>()

    var createVariableOptionIndex = -1
    var inputManuallyOptionIndex = -1

    fun updateOptions(variables: DummyVariableMap) {
        val default = defaultText

        val options = LinkedList<String>()
        val names = LinkedList<String>()
        for ((name, variable) in variables) {
            if (!variableTypes.contains(variable.valueType)) continue

            if (!names.contains(name)) {
                names.add(name)
                options.add(if (variable.description.isEmpty()) name else ("$name §7(${variable.description})"))
            }
        }

        if (default != "" && !names.contains(default)) {
            names.add(default)
            options.add(default)
        }

        if (isOptional()) {
            names.push("optional")
            options.push(Language.get("form.element.variableDropdown.none"))
        }
        if (canSendCreateVariableForm()) {
            names.add("create")
            options.add(Language.get("form.element.variableDropdown.createVariable"))
            createVariableOptionIndex = options.size - 1
        }

        names.add("manually")
        options.add(Language.get("form.element.variableDropdown.inputManually"))
        inputManuallyOptionIndex = options.size - 1

        variableNames = names.toMutableList()
        this.options = options.toMutableList()
        this.default = findDefaultKey(default)
    }

    fun findDefaultKey(default: String): Int {
        if (default == "") return 0

        val key = variableNames.indexOf(default)
        return if (key == -1) 0 else key
    }

    fun updateDefault(default: String) {
        defaultText = default
        this.default = findDefaultKey(default)
    }

    fun isOptional(): Boolean {
        return optional
    }

    fun canSendCreateVariableForm(): Boolean {
        return actions.isNotEmpty()
    }

    fun flattenVariables(variables: DummyVariableMap): DummyVariableMap {
        val flat = mutableMapOf<String, DummyVariable<DummyVariable.Type>>()
        for ((baseName, variable) in variables) {
            flat[baseName] = variable
            for ((propName, value) in variable.getObjectValuesDummy()) {
                if (!value.isObjectVariableType()) {
                    flat["${baseName}.${propName}"] = value
                    continue
                }

                flat.putAll(flattenVariables(mapOf("${baseName}.${propName}" to value)))
            }
        }
        return flat
    }

    fun sendAddVariableForm(player: Player, origin: CustomForm, index: Int) {
        (ListForm("@form.element.variableDropdown.createVariable"))
            .addButtonsEach(actions) { id -> // TODO: List<FlowItem>
                val action = FlowItemFactory.get(id)!!

                Button(action.getName()) {
                    val parents = ArrayDeque(Session.getSession(player).getDeque<FlowItemContainer>("parents"))
                    val container = parents.last()
                    val recipe = parents.removeFirst() as Recipe
                    val variables = recipe.getAddingVariablesBefore(
                        action,
                        parents,
                        FlowItemContainer.ACTION
                    ).toMutableMap()

                    val form = action.getEditForm(variables)
                    form.onReceive { data ->
                        FlowItemForm.onUpdateAction(player, data, form, action) { result ->
                            if (!result) {
                                origin.resend(messages = listOf("@form.cancelled"))
                                return@onUpdateAction
                            }

                            if (container is Recipe) {
                                val place = container.getActions().indexOf(
                                    Session.getSession(player).get<FlowItem>("action_list_clicked")
                                )

                                if (place != -1) {
                                    container.pushItem(place, action, FlowItemContainer.ACTION)
                                } else {
                                    container.addItem(action, FlowItemContainer.ACTION)
                                }
                            } else {
                                val container1 = parents.getOrElse(parents.size - 2) { recipe }
                                val place = container1.getActions().indexOf(container as FlowItem)
                                container1.pushItem(place, action, FlowItemContainer.ACTION)
                            }

                            val add = action.getAddingVariables()
                            variables.putAll(recipe.getAddingVariablesBefore(action, parents, FlowItemContainer.ACTION))
                            variables.putAll(add)

                            val indexes = mutableMapOf<Int, Any>()
                            for ((i, content) in origin.contents.withIndex()) {
                                if (content is VariableDropdown) {
                                    val tmp = content.defaultText
                                    content.updateOptions(variables)
                                    content.updateDefault(if (index == i) add.keys.first() else tmp)
                                    indexes[i] = content.default
                                }
                            }

                            origin.resend(messages = listOf("@form.added"), responseOverrides = indexes)
                        }
                    }.show(player)
                }
            }.addButton(Button("@form.cancelAndBack") {
                origin.resend()
            }).show(player)
    }

    override fun onFormSubmit(response: CustomFormResponse, player: Player) {
        val selectedIndex = response.getDropdownResponse()

        if (isOptional() && selectedIndex == 0) {
            response.overrideResponse("")
            return
        }

        if (selectedIndex == inputManuallyOptionIndex) {
            response.resend = true
            response.overrideElement(
                ExampleInput(text, variableType.name.toLowerCase(), defaultText, !optional),
                defaultText
            )
            return
        }

        if (selectedIndex == createVariableOptionIndex) {
            val index = response.currentIndex
            response.interruptCallback = {
                sendAddVariableForm(player, response.customForm, index)
                true
            }
            return
        }

        response.overrideResponse(variableNames[selectedIndex])
    }
}