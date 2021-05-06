package tokyo.aieuo.mineflow.flowItem.action.script

import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category

class SaveData : FlowItem() {

    override val id = FlowItemIds.SAVE_DATA

    override val nameTranslationKey = "action.saveData.name"
    override val detailTranslationKey = "action.saveData.detail"

    override val category = Category.SCRIPT

    override fun isDataValid(): Boolean {
        return true
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        Main.recipeManager.saveAll()
        Main.formManager.saveAll()
        Main.variableHelper.saveAll()
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
    }

    override fun serializeContents(): List<Any> {
        return listOf()
    }
}
