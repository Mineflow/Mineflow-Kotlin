package tokyo.aieuo.mineflow.ui

import cn.nukkit.Player
import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.exception.InvalidFormValueException
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemContainer
import tokyo.aieuo.mineflow.flowItem.FlowItemFactory
import tokyo.aieuo.mineflow.formAPI.CustomForm
import tokyo.aieuo.mineflow.formAPI.Form
import tokyo.aieuo.mineflow.formAPI.ListForm
import tokyo.aieuo.mineflow.formAPI.ModalForm
import tokyo.aieuo.mineflow.formAPI.element.Button
import tokyo.aieuo.mineflow.formAPI.element.CancelToggle
import tokyo.aieuo.mineflow.formAPI.element.Input
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.recipe.Recipe
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.utils.Session

object FlowItemForm {

    fun sendAddedItemMenu(
        player: Player,
        container: FlowItemContainer,
        type: String,
        action: FlowItem,
        messages: List<String> = listOf()
    ) {
        if (action.hasCustomMenu()) {
            sendFlowItemCustomMenu(player, action, type)
            return
        }

        (ListForm(Language.get("form.${type}.addedItemMenu.title", listOf(container.getContainerName(), action.getName()))))
            .setContent(action.getDetail().trim())
            .addButtons(
                Button("@form.back"),
                Button("@form.edit"),
                Button("@form.move"),
                Button("@form.duplicate"),
                Button("@form.delete"),
            ).onReceive { data ->
                when (data) {
                    0 -> {
                        Session.getSession(player).pop<FlowItemContainer>("parents")
                        FlowItemContainerForm.sendActionList(player, container, type)
                    }
                    1 -> {
                        val parents = ArrayDeque(Session.getSession(player).getDeque<FlowItemContainer>("parents"))
                        val recipe = parents.removeFirst() as Recipe
                        val variables = recipe.getAddingVariablesBefore(action, parents, type)
                        val form = action.getEditForm(variables)
                        form.onReceive { data1 ->
                            onUpdateAction(player, data1, form, action) { result ->
                                sendAddedItemMenu(
                                    player,
                                    container,
                                    type,
                                    action,
                                    listOf(if (result) "@form.changed" else "@form.cancelled")
                                )
                            }
                        }.show(player)
                    }
                    2 -> FlowItemContainerForm.sendMoveAction(
                        player,
                        container,
                        type,
                        container.getItems(type).indexOf(action)
                    )
                    3 -> {
                        val newItem = action.clone()
                        container.addItem(newItem, type)
                        Session.getSession(player).pop<FlowItemContainer>("parents")
                        FlowItemContainerForm.sendActionList(player, container, type, listOf("@form.duplicate.success"))
                    }
                    4 -> sendConfirmDelete(player, action, container, type)
                }
            }.addMessages(messages).show(player)
    }

    fun sendFlowItemCustomMenu(player: Player, action: FlowItem, type: String, messages: List<String> = listOf()) {
        val session = Session.getSession(player)
        val parents = session.getDeque<FlowItemContainer>("parents")
        val parent = parents.last()

        val detail = action.getDetail().trim()
        (ListForm(action.getName()))
            .setContent(if (detail.isEmpty()) "@recipe.noActions" else detail)
            .addButton(
                Button("@form.back") {
                    session.pop<FlowItemContainer>("parents")
                    FlowItemContainerForm.sendActionList(player, parent, FlowItemContainer.ACTION)
                })
            .addButtons(action.getCustomMenuButtons())
            .addButton(
                Button("@form.home.rename.title") {
                    sendChangeName(player, action, parent, FlowItemContainer.ACTION)
                })
            .addButton(
                Button("@form.move") {
                    FlowItemContainerForm.sendMoveAction(
                        player,
                        parent,
                        FlowItemContainer.ACTION,
                        parent.getActions().indexOf(action)
                    )
                })
            .addButton(
                Button("@form.duplicate") {
                    val newItem = action.clone()
                    parent.addItem(newItem, type)
                    Session.getSession(player).pop<FlowItemContainer>("parents")
                    FlowItemContainerForm.sendActionList(
                        player,
                        parent,
                        type,
                        listOf("@form.duplicate.success")
                    )
                })
            .addButton(
                Button("@form.delete") {
                    sendConfirmDelete(player, action, parent, FlowItemContainer.ACTION)
                })
            .addMessages(messages)
            .show(player)
    }

    fun onUpdateAction(player: Player, data: List<*>, form: Form, action: FlowItem, callback: (Boolean) -> Unit) {
        val cancelChecked = data.last() as Boolean

        if (cancelChecked) {
            callback(false)
            return
        }

        val values = try {
            action.parseFromFormData(CustomFormResponseList(data.subList(1, data.lastIndex)))
        } catch (e: InvalidFormValueException) {
            form.resend((e.message ?: return) to e.index + 1)
            return
        }

        try {
            val contents = if (values is CustomFormResponseList) values else CustomFormResponseList(values)
            action.loadSaveData(contents)
        } catch (e: Exception) {
            Language.get("action.error.recipe").let {
                player.sendMessage(it)
                Main.instance.logger.error(it, e)
            }
            return
        }
        callback(true)
    }

    fun selectActionCategory(player: Player, container: FlowItemContainer, type: String) {
        val buttons = mutableListOf(
            Button("@form.back") {
                Session.getSession(player).pop<FlowItemContainer>("parents")
                FlowItemContainerForm.sendActionList(player, container, type)
            },
            Button("@form.items.category.favorite") {
                val favorites = Main.instance.playerSettings.getFavorites(player.name, type)
                val actions = mutableListOf<FlowItem>()
                for (favorite in favorites) {
                    val action = FlowItemFactory.get(favorite)
                    if (action === null) continue

                    actions.add(action)
                }
                Session.getSession(player)
                    .set("flowItem_category", Language.get("form.items.category.favorite"))
                sendSelectAction(player, container, type, actions)
            }
        )

        for (category in Category.categories) {
            buttons.add(Button("@category.$category") {
                val isCondition = type == FlowItemContainer.CONDITION
                val actions = FlowItemFactory.getByFilter(
                    category,
                    Main.instance.playerSettings.get("${player.name}.permission", 0),
                    !isCondition,
                    isCondition
                )

                Session.getSession(player)
                    .set("flowItem_category", Language.get("category.$category"))
                sendSelectAction(player, container, type, actions)
            })
        }

        buttons.add(Button("@form.search") {
            sendSearchAction(player, container, type)
        })

        (ListForm(Language.get("form.${type}.category.title", listOf(container.getContainerName()))))
            .addButtons(buttons)
            .show(player)
    }

    fun sendSearchAction(player: Player, container: FlowItemContainer, type: String) {
        (CustomForm(Language.get("form.${type}.search.title", listOf(container.getContainerName()))))
            .setContents(mutableListOf(
                Input(
                    "@form.items.search.keyword",
                    "",
                    Session.getSession(player).getString("flowItem_search"),
                    true
                ),
                CancelToggle { selectActionCategory(player, container, type) }
            )).onReceive { data ->
                val name = data.getString(0)
                val isCondition = type == FlowItemContainer.CONDITION
                val permission = Main.instance.playerSettings.get("${player.name}.permission", 0)
                val actions = FlowItemFactory.getByFilter(null, permission, !isCondition, isCondition).filter {
                    it.getName().toLowerCase().contains(name.toLowerCase())
                }

                Session.getSession(player).set("flowItem_search", name)
                Session.getSession(player)
                    .set("flowItem_category", Language.get("form.items.category.search", listOf(name)))
                sendSelectAction(player, container, type, actions)
            }.show(player)
    }

    fun sendSelectAction(player: Player, container: FlowItemContainer, type: String, items: List<FlowItem>) {
        val buttons = mutableListOf(
            Button("@form.back") { selectActionCategory(player, container, type) }
        )
        for (item in items) {
            buttons.add(Button(item.getName()))
        }

        (ListForm(Language.get("form.${type}.select.title", listOf(container.getContainerName(), Session.getSession(player).getString("flowItem_category")))))
            .setContent(if (buttons.size == 1) "@form.action.empty" else "@form.selectButton")
            .addButtons(buttons)
            .onReceive { data ->
                Session.getSession(player).set("${type}s", items)
                val item = items[data - 1].clone()
                sendActionMenu(player, container, type, item)
            }.show(player)
    }

    fun sendActionMenu(
        player: Player,
        container: FlowItemContainer,
        type: String,
        item: FlowItem,
        messages: List<String> = listOf()
    ) {
        val favorites = Main.instance.playerSettings.getFavorites(player.name, type)

        (ListForm(Language.get("form.${type}.menu.title", listOf(container.getContainerName(), item.id))))
            .setContent(item.getDescription())
            .addButtons(
                Button("@form.back"),
                Button("@form.add"),
                Button(if (item.id in favorites) "@form.items.removeFavorite" else "@form.items.addFavorite"),
            ).onReceive { data ->
                when (data) {
                    0 -> {
                        val actions = Session.getSession(player).getList<FlowItem>("${type}s")
                        sendSelectAction(player, container, type, actions)
                    }
                    1 -> {
                        if (item.hasCustomMenu()) {
                            container.addItem(item, type)
                            sendFlowItemCustomMenu(player, item, type)
                            return@onReceive
                        }

                        val parents = ArrayDeque(Session.getSession(player).getDeque<FlowItemContainer>("parents"))
                        val recipe = parents.removeFirst() as Recipe
                        val variables = recipe.getAddingVariablesBefore(item, parents, type)
                        val form = item.getEditForm(variables)

                        form.onReceive { data1 ->
                            onUpdateAction(player, data1, form, item) { result ->
                                if (result) {
                                    container.addItem(item, type)
                                    Session.getSession(player).pop<FlowItemContainer>("parents")
                                    FlowItemContainerForm.sendActionList(player, container, type, listOf("@form.added"))
                                } else {
                                    sendActionMenu(player, container, type, item, listOf("@form.cancelled"))
                                }
                            }
                        }.show(player)
                    }
                    2 -> {
                        Main.instance.playerSettings.let { config ->
                            config.toggleFavorite(player.name, type, item.id)
                            config.save()
                        }
                        sendActionMenu(player, container, type, item, listOf("@form.changed"))
                    }
                }
            }.addMessages(messages).show(player)
    }

    fun sendConfirmDelete(player: Player, action: FlowItem, container: FlowItemContainer, type: String) {
        (ModalForm(Language.get("form.items.delete.title", listOf(container.getContainerName(), action.getName()))))
            .setContent(Language.get("form.delete.confirm", listOf(action.getDetail().trim())))
            .onYes {
                val index = container.getItems(type).indexOf(action)
                container.removeItem(index, type)
                Session.getSession(player).pop<FlowItemContainer>("parents")
                FlowItemContainerForm.sendActionList(player, container, type, listOf("@form.deleted"))
            }.onNo {
                if (container is FlowItem && container.hasCustomMenu()) {
                    sendFlowItemCustomMenu(player, container, type, listOf("@form.cancelled"))
                } else {
                    sendAddedItemMenu(player, container, type, action, listOf("@form.cancelled"))
                }
            }.show(player)
    }

    fun sendChangeName(player: Player, item: FlowItem, container: FlowItemContainer, type: String) {
        (CustomForm(Language.get("form.recipe.changeName.title", listOf(item.getName()))))
            .setContents(mutableListOf(
                Input("@form.recipe.changeName.content1", "", item.customName),
                CancelToggle {
                    if (container is FlowItem && container.hasCustomMenu()) {
                        sendFlowItemCustomMenu(player, container, type, listOf("@form.cancelled"))
                    } else {
                        sendAddedItemMenu(player, container, type, item, listOf("@form.cancelled"))
                    }
                }
            )).onReceive { data ->
                item.customName = data.getString(0)
                if (container is FlowItem && container.hasCustomMenu()) {
                    sendFlowItemCustomMenu(player, container, type, listOf("@form.changed"))
                } else {
                    sendAddedItemMenu(player, container, type, item, listOf("@form.changed"))
                }
            }.show(player)
    }
}