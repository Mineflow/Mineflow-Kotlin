package tokyo.aieuo.mineflow.ui

import cn.nukkit.Player
import cn.nukkit.utils.TextFormat
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemContainer
import tokyo.aieuo.mineflow.formAPI.ListForm
import tokyo.aieuo.mineflow.formAPI.element.Button
import tokyo.aieuo.mineflow.recipe.Recipe
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.utils.Session

object FlowItemContainerForm {

    fun sendActionList(player: Player, container: FlowItemContainer, type: String, messages: List<String> = listOf()) {
        val actions = container.getItems(type)

        val buttons = mutableListOf(Button("@form.back"), Button("@$type.add"))
        for (action in actions) {
            buttons.add(Button(if (action.customName.isEmpty()) TextFormat.clean(action.getDetail()) else action.customName))
        }

        (ListForm(Language.get("form.${type}Container.list.title", listOf(container.getContainerName()))))
            .addButtons(buttons)
            .onReceive { data ->
                if (data == 0) {
                    if (container is Recipe) {
                        RecipeForm.sendRecipeMenu(player, container)
                    } else {
                        FlowItemForm.sendFlowItemCustomMenu(player, container as FlowItem, type)
                    }
                    return@onReceive
                }
                Session.getSession(player).let {
                    it.remove("action_list_clicked")
                    it.push("parents", container)
                }

                if (data == 1) {
                    FlowItemForm.selectActionCategory(player, container, type)
                    return@onReceive
                }
                val action = actions[data - 2]
                Session.getSession(player).set("action_list_clicked", action)

                FlowItemForm.sendAddedItemMenu(player, container, type, action)
            }.addMessages(messages).show(player)
    }

    fun sendMoveAction(
        player: Player,
        container: FlowItemContainer,
        type: String,
        selected: Int,
        messages: List<String> = listOf(),
        count: Int = 0
    ) {
        val actions = container.getItems(type)
        val selectedAction = actions[selected]

        val parents = ArrayDeque(Session.getSession(player).getDeque<FlowItemContainer>("parents"))
        parents.removeLastOrNull()
        val parent = parents.removeLastOrNull()

        val buttons = mutableListOf(
            Button("@form.back") {
                FlowItemForm.sendAddedItemMenu(
                    player,
                    container,
                    type,
                    actions[selected],
                    listOf(if (count == 0) "@form.cancelled" else "@form.moved")
                )
            },
        )

        if (parent is FlowItemContainer) {
            buttons.add(Button("@action.move.outside") {
                val tmp = container.getItem(selected, type)
                container.removeItem(selected, type)
                if (tmp != null) parent.addItem(tmp, type)

                Session.getSession(player).pop<FlowItem>("parents")
                sendMoveAction(player, parent, type, parent.getItems(type).size - 1, listOf("@form.moved"), count + 1)
            })
        }

        for ((i, action) in actions.withIndex()) {
            if (i != selected && i != selected + 1) {
                buttons.add(Button("@form.move.to.here") {
                    moveContent(player, container, type, actions, selected, i, count)
                })
            }

            val color = if (i == selected) TextFormat.AQUA.toString() else ""
            buttons.add(Button(color + TextFormat.clean(action.getDetail()).trim()) {
                if (i == selected || action !is FlowItemContainer) {
                    sendMoveAction(player, container, type, selected, listOf("@form.move.target.invalid"), count)
                } else {
                    val tmp = container.getItem(selected, type)
                    container.removeItem(selected, type)
                    if (tmp != null) action.addItem(tmp, type)
                    Session.getSession(player).push("parents", action)
                    sendMoveAction(
                        player,
                        action,
                        type,
                        action.getItems(type).size - 1,
                        listOf("@form.moved"),
                        count + 1
                    )
                }
            })
        }
        if (selected != actions.size - 1) {
            buttons.add(Button("@form.move.to.here") {
                moveContent(player, container, type, actions, selected, actions.size, count)
            })
        }

        (ListForm(
            Language.get(
                "form.${type}Container.move.title",
                listOf(container.getContainerName(), selectedAction.getName())
            )
        ))
            .setContent("@form.${type}Container.move.content")
            .addButtons(buttons)
            .addMessages(messages)
            .show(player)
    }

    fun moveContent(
        player: Player,
        container: FlowItemContainer,
        type: String,
        actions: List<FlowItem>,
        from: Int,
        to: Int,
        count: Int
    ) {
        container.setItems(getMovedContents(actions, from, to), type)
        sendMoveAction(player, container, type, if (from < to) to - 1 else to, listOf("@form.moved"), count + 1)
    }

    fun getMovedContents(_contents: List<FlowItem>, from: Int, _to: Int): MutableList<FlowItem> {
        val contents = _contents.toMutableList()
        val to = if (from < _to) _to - 1 else _to

        val move = contents[from]
        contents.removeAt(from)
        contents.add(to, move)
        return contents
    }
}