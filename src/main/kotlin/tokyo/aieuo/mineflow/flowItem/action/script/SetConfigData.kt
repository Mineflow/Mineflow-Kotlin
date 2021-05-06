package tokyo.aieuo.mineflow.flowItem.action.script

import tokyo.aieuo.mineflow.Main
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
import tokyo.aieuo.mineflow.utils.is_numeric
import tokyo.aieuo.mineflow.variable.ListVariable
import tokyo.aieuo.mineflow.variable.MapVariable
import tokyo.aieuo.mineflow.variable.NumberVariable

class SetConfigData(config: String = "", var key: String = "", var value: String = "") : FlowItem(),
    ConfigFileFlowItem {

    override val id = FlowItemIds.SET_CONFIG_VALUE

    override val nameTranslationKey = "action.setConfigData.name"
    override val detailTranslationKey = "action.setConfigData.detail"
    override val detailDefaultReplaces = listOf("config", "key", "value")

    override val category = Category.SCRIPT

    override val permission = PERMISSION_LEVEL_2

    override var configVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setConfigVariableName(config)
    }

    override fun isDataValid(): Boolean {
        return getConfigVariableName() != "" && key != "" && value != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getConfigVariableName(), key, value))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val key = source.replaceVariables(key)

        val valueStr = value
        var value: Any = value

        val helper = Main.variableHelper
        if (helper.isVariableString(valueStr)) {
            val variable = valueStr.substring(1, valueStr.length - 1).let {
                source.getVariable(it) ?: helper.get(it)
            } ?: value

            value = when (variable) {
                is MapVariable -> variable.toArray()
                is ListVariable -> variable.toArray()
                is NumberVariable -> variable.value
                else -> variable.toString()
            }
        } else {
            value = helper.replaceVariables(valueStr, source.getVariables())
            if (is_numeric(value)) value = value.toFloat()
        }

        val config = getConfig(source)

        config.set(key, value)
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            ConfigVariableDropdown(variables, getConfigVariableName()),
            ExampleInput("@action.setConfigData.form.key", "aieuo", key, true),
            ExampleInput("@action.setConfigData.form.value", "100", value, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setConfigVariableName(contents.getString(0))
        key = contents.getString(1)
        value = contents.getString(2)
    }

    override fun serializeContents(): List<Any> {
        return listOf(getConfigVariableName(), key, value)
    }

    override fun clone(): SetConfigData {
        val item = super.clone() as SetConfigData
        item.configVariableNames = configVariableNames.toMutableMap()
        return item
    }
}