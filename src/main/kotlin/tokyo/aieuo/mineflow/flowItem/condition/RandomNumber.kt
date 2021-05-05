package tokyo.aieuo.mineflow.flowItem.condition

import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleNumberInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable
import kotlin.math.max
import kotlin.math.min

class RandomNumber(var min: String = "", var max: String = "", var value: String = ""): FlowItem(), Condition {

    override val id = FlowItemIds.RANDOM_NUMBER

    override val nameTranslationKey = "condition.randomNumber.name"
    override val detailTranslationKey = "condition.randomNumber.detail"
    override val detailDefaultReplaces = listOf("min", "max", "value")

    override val category = Category.MATH

    override fun isDataValid(): Boolean {
        return min != "" && max != "" && value != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(min, max, value))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val min = source.replaceVariables(min)
        val max = source.replaceVariables(max)
        val value = source.replaceVariables(value)

        throwIfInvalidNumber(min)
        throwIfInvalidNumber(max)
        throwIfInvalidNumber(value)

        val result = (min(min.toInt(), max.toInt())..max(min.toInt(), max.toInt())).random() == value.toInt()
        yield(if (result) FlowItemExecutor.Result.SUCCESS else FlowItemExecutor.Result.FAILURE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            ExampleNumberInput("@condition.randomNumber.form.min", "0", min, true),
            ExampleNumberInput("@condition.randomNumber.form.max", "10", max, true),
            ExampleNumberInput("@condition.randomNumber.form.value", "0", value, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        min = contents.getString(0)
        max = contents.getString(1)
        value = contents.getString(2)
    }

    override fun serializeContents(): List<Any> {
        return listOf(min, max, value)
    }
}