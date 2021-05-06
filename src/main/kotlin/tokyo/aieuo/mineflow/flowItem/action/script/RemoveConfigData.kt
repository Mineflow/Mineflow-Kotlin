package tokyo.aieuo.mineflow.flowItem.action.script

import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.ConfigFileFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ConfigVariableDropdown
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language

class RemoveConfigData(config: String = "", var key: String = "") : FlowItem(), ConfigFileFlowItem {

    override val id = FlowItemIds.REMOVE_CONFIG_VALUE

    override val nameTranslationKey = "action.removeConfigData.name"
    override val detailTranslationKey = "action.removeConfigData.detail"
    override val detailDefaultReplaces = listOf("config", "key")

    override val category = Category.SCRIPT

    override val permission = PERMISSION_LEVEL_2

    override var configVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setConfigVariableName(config)
    }

    override fun isDataValid(): Boolean {
        return getConfigVariableName() != "" && key != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getConfigVariableName(), key))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val key = source.replaceVariables(key)

        val config = getConfig(source)

        config.remove(key)
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            ConfigVariableDropdown(variables, getConfigVariableName()),
            ExampleInput("@action.setConfigData.form.key", "aieuo", key, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setConfigVariableName(contents.getString(0))
        key = contents.getString(1)
    }

    override fun serializeContents(): List<Any> {
        return listOf(getConfigVariableName(), key)
    }

    override fun clone(): RemoveConfigData {
        val item = super.clone() as RemoveConfigData
        item.configVariableNames = configVariableNames.toMutableMap()
        return item
    }
}