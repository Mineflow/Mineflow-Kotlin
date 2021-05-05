package tokyo.aieuo.mineflow.flowItem.action.script

import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.ConfigFileFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ConfigVariableDropdown
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable

class SaveConfigFile(config: String = ""): FlowItem(), ConfigFileFlowItem {

    override val id = FlowItemIds.SAVE_CONFIG_FILE

    override val nameTranslationKey = "action.saveConfigFile.name"
    override val detailTranslationKey = "action.saveConfigFile.detail"
    override val detailDefaultReplaces = listOf("config")

    override val category = Category.SCRIPT

    override val permission = PERMISSION_LEVEL_2

    override var configVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setConfigVariableName(config)
    }

    override fun isDataValid(): Boolean {
        return getConfigVariableName() != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getConfigVariableName()))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val config = getConfig(source)

        config.save()
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            ConfigVariableDropdown(variables, getConfigVariableName()),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setConfigVariableName(contents.getString(0))
    }

    override fun serializeContents(): List<Any> {
        return listOf(getConfigVariableName())
    }

    override fun clone(): SaveConfigFile {
        val item = super.clone() as SaveConfigFile
        item.configVariableNames = configVariableNames.toMutableMap()
        return item
    }
}