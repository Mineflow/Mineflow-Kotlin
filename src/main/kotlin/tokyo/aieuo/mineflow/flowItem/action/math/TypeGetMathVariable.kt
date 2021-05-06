package tokyo.aieuo.mineflow.flowItem.action.math

import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable

abstract class TypeGetMathVariable(var resultName: String = "") : FlowItem() {

    override val detailDefaultReplaces = listOf("result")

    override val category = Category.MATH

    override fun isDataValid(): Boolean {
        return resultName != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(resultName))
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            ExampleInput("@action.form.resultVariableName", "result", resultName, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        if (contents.isNotEmpty()) resultName = contents.getString(0)
    }

    override fun serializeContents(): List<Any> {
        return listOf(resultName)
    }

    override fun getAddingVariables(): DummyVariableMap {
        return mapOf(
            resultName to DummyVariable(DummyVariable.Type.NUMBER)
        )
    }
}