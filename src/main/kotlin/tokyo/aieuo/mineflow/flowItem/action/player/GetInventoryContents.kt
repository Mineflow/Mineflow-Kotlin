package tokyo.aieuo.mineflow.flowItem.action.player

import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.PlayerFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.element.mineflow.PlayerVariableDropdown
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.ListVariable
import tokyo.aieuo.mineflow.variable.obj.ItemObjectVariable

@Suppress("LeakingThis")
open class GetInventoryContents(player: String = "", var resultName: String = "inventory") : FlowItem(),
    PlayerFlowItem {

    override val id = FlowItemIds.GET_INVENTORY_CONTENTS

    override val nameTranslationKey = "action.getInventory.name"
    override val detailTranslationKey = "action.getInventory.detail"
    override val detailDefaultReplaces = listOf("player", "inventory")

    override val category = Category.PLAYER

    override var playerVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setPlayerVariableName(player)
    }

    override fun isDataValid(): Boolean {
        return getPlayerVariableName() != "" && resultName != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getPlayerVariableName(), resultName))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val resultName = source.replaceVariables(resultName)

        val entity = getPlayer(source)
        throwIfInvalidPlayer(entity)

        val variable = ListVariable(entity.inventory.contents.map { ItemObjectVariable(it.value) })

        source.addVariable(resultName, variable)
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            PlayerVariableDropdown(variables, getPlayerVariableName()),
            ExampleInput("@action.form.resultVariableName", "inventory", resultName, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setPlayerVariableName(contents.getString(0))
        resultName = contents.getString(1)
    }

    override fun serializeContents(): List<Any> {
        return listOf(getPlayerVariableName(), resultName)
    }

    override fun getAddingVariables(): DummyVariableMap {
        return mapOf(
            resultName to DummyVariable(DummyVariable.Type.LIST, DummyVariable.Type.ITEM)
        )
    }

    override fun clone(): GetInventoryContents {
        val item = super.clone() as GetInventoryContents
        item.playerVariableNames = playerVariableNames.toMutableMap()
        return item
    }
}