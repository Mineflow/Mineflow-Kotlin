package tokyo.aieuo.mineflow.flowItem.condition

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

class ExistsConfigData(config: String = "", var key: String = "") : FlowItem(), Condition, ConfigFileFlowItem {

    override val id = FlowItemIds.EXISTS_CONFIG_DATA

    override val nameTranslationKey = "condition.existsConfigData.name"
    override val detailTranslationKey = "condition.existsConfigData.detail"
    override val detailDefaultReplaces = listOf("config", "key")

    override val category = Category.SCRIPT

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

        val config = getConfig(source)

        val key = source.replaceVariables(key)

        val result = config.get(key) !== null
        yield(if (result) FlowItemExecutor.Result.SUCCESS else FlowItemExecutor.Result.FAILURE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            ConfigVariableDropdown(variables, getConfigVariableName()),
            ExampleInput("@condition.existsConfigData.form.key", "aieuo", key, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setConfigVariableName(contents.getString(0))
        key = contents.getString(1)
    }

    override fun serializeContents(): List<Any> {
        return listOf(getConfigVariableName(), key)
    }

    override fun clone(): ExistsConfigData {
        val item = super.clone() as ExistsConfigData
        item.configVariableNames = configVariableNames.toMutableMap()
        return item
    }
}