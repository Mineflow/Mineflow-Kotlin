package tokyo.aieuo.mineflow.flowItem.action.event

import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.trigger.TriggerHolder
import tokyo.aieuo.mineflow.trigger.custom.CustomTrigger
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language

class CallCustomTrigger(var triggerName: String = "") : FlowItem() {

    override val id = FlowItemIds.CALL_CUSTOM_TRIGGER

    override val nameTranslationKey = "action.callTrigger.name"
    override val detailTranslationKey = "action.callTrigger.detail"
    override val detailDefaultReplaces = listOf("identifier")

    override val category = Category.EVENT

    override fun isDataValid(): Boolean {
        return triggerName != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(triggerName))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val name = source.replaceVariables(triggerName)
        val trigger = CustomTrigger.create(name)
        val recipes = TriggerHolder.getRecipes(trigger)
        recipes?.executeAll(source.target, mapOf(), source.event)
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            ExampleInput("@action.callTrigger.form.identifier", "aieuo", triggerName, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        triggerName = contents.getString(0)
    }

    override fun serializeContents(): List<Any> {
        return listOf(triggerName)
    }
}