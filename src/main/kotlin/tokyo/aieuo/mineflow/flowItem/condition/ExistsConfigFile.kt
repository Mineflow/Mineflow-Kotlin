package tokyo.aieuo.mineflow.flowItem.condition

import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.exception.InvalidFormValueException
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language
import java.nio.file.Files
import java.nio.file.Path

class ExistsConfigFile(var fileName: String = "") : FlowItem(), Condition {

    override val id = FlowItemIds.EXISTS_CONFIG_FILE

    override val nameTranslationKey = "condition.existsConfigFile.name"
    override val detailTranslationKey = "condition.existsConfigFile.detail"
    override val detailDefaultReplaces = listOf("name")

    override val category = Category.SCRIPT

    override fun isDataValid(): Boolean {
        return fileName != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(fileName))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val name = source.replaceVariables(fileName).replace(Regex("[.¥/:?<>|*\"]"), "")

        yield(FlowItemExecutor.Result.CONTINUE)
        val result = Files.exists(Path.of("${Main.instance.dataFolder.path}/configs/${name}.yml"))
        yield(if (result) FlowItemExecutor.Result.SUCCESS else FlowItemExecutor.Result.FAILURE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            ExampleInput("@action.createConfigVariable.form.name", "config", fileName, true),
        )
    }

    override fun parseFromFormData(data: CustomFormResponseList): List<Any?> {
        if (Regex("[.¥/:?<>|*\"]").containsMatchIn(data.getString(0))) {
            throw InvalidFormValueException("@form.recipe.invalidName", 0)
        }
        return listOf(data[0])
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        fileName = contents.getString(0)
    }

    override fun serializeContents(): List<Any> {
        return listOf(fileName)
    }
}