package tokyo.aieuo.mineflow.flowItem.action.script

import cn.nukkit.Player
import cn.nukkit.level.Position
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemContainer
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.PositionFlowItem
import tokyo.aieuo.mineflow.formAPI.CustomForm
import tokyo.aieuo.mineflow.formAPI.element.Button
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.element.mineflow.PositionVariableDropdown
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.recipe.Recipe
import tokyo.aieuo.mineflow.ui.FlowItemContainerForm
import tokyo.aieuo.mineflow.ui.FlowItemForm
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Session
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.obj.PositionObjectVariable
import kotlin.math.max
import kotlin.math.min

class ForeachPosition(
    pos1: String = "pos1",
    pos2: String = "pos2",
    actions: List<FlowItem> = listOf(),
    override var customName: String = ""
) : FlowItem(), FlowItemContainer, PositionFlowItem {

    override val id = FlowItemIds.FOREACH_POSITION

    override val nameTranslationKey = "action.foreachPosition.name"
    override val detailTranslationKey = "action.foreachPosition.description"

    override val category = Category.SCRIPT

    override val permission = PERMISSION_LEVEL_1

    var counterName = "pos"

    override var positionVariableNames: MutableMap<String, String> = mutableMapOf()
    override var items: MutableMap<String, MutableList<FlowItem>> = mutableMapOf()

    init {
        setPositionVariableName(pos1, "pos1")
        setPositionVariableName(pos2, "pos2")
        setItems(actions.toMutableList(), FlowItemContainer.ACTION)
    }

    override fun getDetail(): String {
        val repeat = "${getPositionVariableName("pos1")} => ${getPositionVariableName("pos2")}; (${counterName})"

        val details = mutableListOf("", "§7==§f eachPos($repeat) §7==§f")
        for (action in getActions()) {
            details.add(action.getDetail())
        }
        details.add("§7================================§f")
        return details.joinToString("\n")
    }

    override fun getContainerName(): String {
        return if (customName.isEmpty()) getName() else customName
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        val counterName = source.replaceVariables(counterName)

        val pos1 = getPosition(source, "pos1")
        val pos2 = getPosition(source, "pos2")

        val (sx, ex) = min(pos1.x, pos2.x) to max(pos1.x, pos2.x)
        val (sy, ey) = min(pos1.y, pos2.y) to max(pos1.y, pos2.y)
        val (sz, ez) = min(pos1.z, pos2.z) to max(pos1.z, pos2.z)

        for (x in sx.toInt()..ex.toInt()) {
            for (y in sy.toInt()..ey.toInt()) {
                for (z in sz.toInt()..ez.toInt()) {
                    val pos = Position(x.toDouble(), y.toDouble(), z.toDouble(), pos1.level)

                    yieldAll(
                        FlowItemExecutor(
                            getActions(), source.target, mutableMapOf(
                                counterName to PositionObjectVariable(pos, counterName)
                            ), source
                        ).executeGenerator()
                    )
                }
            }
        }

        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun hasCustomMenu(): Boolean {
        return true
    }

    override fun getCustomMenuButtons(): List<Button> {
        return listOf(
            Button("@action.edit") { player ->
                FlowItemContainerForm.sendActionList(player, this, FlowItemContainer.ACTION)
            },
            Button("@action.for.setting") { player ->
                val parents = ArrayDeque(Session.getSession(player).getDeque<FlowItemContainer>("parents"))
                val recipe = parents.removeFirst() as Recipe
                val variables = recipe.getAddingVariablesBefore(this, parents, FlowItemContainer.ACTION)
                sendSettingCounter(player, variables)
            },
        )
    }

    fun sendSettingCounter(player: Player, variables: DummyVariableMap) {
        val action = this
        (CustomForm("@action.for.setting"))
            .setContents(
                mutableListOf(
                    PositionVariableDropdown(
                        variables,
                        getPositionVariableName("pos1"),
                        "@action.foreachPosition.form.pos1"
                    ),
                    PositionVariableDropdown(
                        variables,
                        getPositionVariableName("pos2"),
                        "@action.foreachPosition.form.pos2"
                    ),
                    ExampleInput("@action.for.counterName", "pos", counterName, true),
                )
            ).onReceive { data ->
                setPositionVariableName(data.getString(0), "pos1")
                setPositionVariableName(data.getString(1), "pos2")
                counterName = data.getString(2)
                FlowItemForm.sendFlowItemCustomMenu(player, action, FlowItemContainer.ACTION, listOf("@form.changed"))
            }.show(player)
    }

    @Suppress("UNCHECKED_CAST")
    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        for (content in contents[0] as List<Map<String, Any>>) {
            val action = loadEachSaveData(content)
            addItem(action, FlowItemContainer.ACTION)
        }

        setPositionVariableName(contents.getString(1), "pos1")
        setPositionVariableName(contents.getString(2), "pos2")
        counterName = contents.getString(3)
    }

    override fun serializeContents(): List<Any> {
        return listOf(
            getActions(),
            getPositionVariableName("pos1"),
            getPositionVariableName("pos2"),
            counterName,
        )
    }

    override fun getAddingVariables(): DummyVariableMap {
        return mapOf(
            counterName to DummyVariable(DummyVariable.Type.POSITION),
        )
    }

    override fun isDataValid(): Boolean {
        return true
    }

    override fun clone(): ForeachPosition {
        val item = super.clone() as ForeachPosition

        item.positionVariableNames = positionVariableNames.toMutableMap()

        item.items = mutableMapOf()
        val actions = mutableListOf<FlowItem>()
        for (action in getActions()) {
            actions.add(action.clone())
        }
        item.setItems(actions, FlowItemContainer.ACTION)

        return item
    }
}