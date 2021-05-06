package tokyo.aieuo.mineflow.flowItem.action.player

import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.variable.ListVariable
import tokyo.aieuo.mineflow.variable.obj.ItemObjectVariable

class GetArmorInventoryContents(player: String = "", resultName: String = "inventory") :
    GetInventoryContents(player, resultName) {

    override val id = FlowItemIds.GET_ARMOR_INVENTORY_CONTENTS

    override val nameTranslationKey = "action.getArmorInventory.name"
    override val detailTranslationKey = "action.getArmorInventory.detail"

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val resultName = source.replaceVariables(resultName)

        val entity = getPlayer(source)
        throwIfInvalidPlayer(entity)

        val variable = ListVariable(entity.inventory.armorContents.map { ItemObjectVariable(it) })

        source.addVariable(resultName, variable)
        yield(FlowItemExecutor.Result.CONTINUE)
    }
}