package tokyo.aieuo.mineflow.flowItem

import tokyo.aieuo.mineflow.exception.InvalidFlowValueException
import tokyo.aieuo.mineflow.formAPI.CustomForm
import tokyo.aieuo.mineflow.formAPI.element.Button
import tokyo.aieuo.mineflow.formAPI.element.CancelToggle
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.Label
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.JsonSerializable
import tokyo.aieuo.mineflow.utils.Language

interface IFlowItem : JsonSerializable {

    val id: String
    val nameTranslationKey: String
    val detailTranslationKey: String

    val detailDefaultReplaces: List<String>

    val category: String

    var customName: String

    val permission: Int

    fun getName(): String {
        return Language.get(nameTranslationKey)
    }

    fun getDetail(): String {
        return Language.get(detailTranslationKey)
    }

    fun getDescription(): String {
        val replaces = detailDefaultReplaces.map { "§7<$it>§f" }
        return Language.get(detailTranslationKey, replaces)
    }

    override fun jsonSerialize(): Map<String, Any?> {
        val data = mutableMapOf(
            "id" to id,
            "contents" to serializeContents(),
        )
        if (customName.isNotEmpty()) {
            data["customName"] = customName
        }
        return data
    }

    fun throwIfCannotExecute() {
        if (!isDataValid()) {
            throw InvalidFlowValueException(Language.get("invalid.contents"))
        }
    }

    fun throwIfInvalidNumber(
        numberStr: String,
        min: Double? = null,
        max: Double? = null,
        exclude: List<Double> = listOf()
    ) {
        val number = numberStr.toDoubleOrNull()
        if (number === null) {
            throw InvalidFlowValueException(Language.get("action.error.notNumber", listOf(numberStr)))
        }
        if (min !== null && number < min) {
            throw InvalidFlowValueException(Language.get("action.error.lessValue", listOf(min.toString(), numberStr)))
        }
        if (max !== null && number > max) {
            throw InvalidFlowValueException(Language.get("action.error.overValue", listOf(max.toString(), numberStr)))
        }
        if (exclude.isNotEmpty() && exclude.contains(number)) {
            throw InvalidFlowValueException(
                Language.get(
                    "action.error.excludedNumber", listOf(
                        exclude.joinToString(", "), numberStr
                    )
                )
            )
        }
    }

    fun getEditForm(variables: DummyVariableMap): CustomForm {
        return (CustomForm(getName()))
            .addContent(Label(getDescription()))
            .addContents(getEditFormElements(variables))
            .addContent(CancelToggle())
    }

    fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf()
    }

    fun parseFromFormData(data: CustomFormResponseList): List<Any?> {
        return data
    }

    fun hasCustomMenu(): Boolean {
        return false
    }

    fun getCustomMenuButtons(): List<Button> {
        return listOf()
    }

    fun allowDirectCall(): Boolean {
        return true
    }

    fun getAddingVariables(): DummyVariableMap {
        return mapOf()
    }

    fun isDataValid(): Boolean

    fun serializeContents(): List<Any>

    fun loadSaveData(contents: CustomFormResponseList): IFlowItem

    fun execute(source: FlowItemExecutor): Sequence<FlowItemExecutor.Result>
}
