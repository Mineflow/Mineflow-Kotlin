package tokyo.aieuo.mineflow.flowItem.action.player

import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable

abstract class TypeMessage(var message: String = ""): FlowItem() {

    override val detailDefaultReplaces = listOf("message")

    override val category = Category.PLAYER

    override fun isDataValid(): Boolean {
        return message != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(message))
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            ExampleInput("@action.message.form.message", "aieuo", message, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        message = contents.getString(0)
    }

    override fun serializeContents(): List<Any> {
        return listOf(message)
    }
}