package tokyo.aieuo.mineflow.flowItem.action.script

import cn.nukkit.scheduler.NukkitRunnable
import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable

class Wait(var time: String = ""): FlowItem() {

    override val id = FlowItemIds.ACTION_WAIT

    override val nameTranslationKey = "action.wait.name"
    override val detailTranslationKey = "action.wait.detail"
    override val detailDefaultReplaces = listOf("time")

    override val category = Category.SCRIPT

    override fun isDataValid(): Boolean {
        return time != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(time))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val time = source.replaceVariables(time)
        throwIfInvalidNumber(time, 1.0 / 20)

        object: NukkitRunnable() {
            override fun run() {
                source.resume()
            }
        }.runTaskLater(Main.instance, (time.toFloat() * 20).toInt())
        yield(FlowItemExecutor.Result.AWAIT)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            ExampleInput("@action.wait.form.time", "10", time, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        time = contents.getString(0)
    }

    override fun serializeContents(): List<Any> {
        return listOf(time)
    }
}
