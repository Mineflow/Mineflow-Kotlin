package tokyo.aieuo.mineflow.flowItem.action.player

import tokyo.aieuo.mineflow.exception.InvalidFormValueException
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.PlayerFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleNumberInput
import tokyo.aieuo.mineflow.formAPI.element.mineflow.PlayerVariableDropdown
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable

class SendTitle(player: String = "", var title: String = "", var subTitle: String = "",
                var fadeIn: String = "-1", var stay: String = "-1", var fadeOut: String = "-1")
    : FlowItem(), PlayerFlowItem {

    override val id = FlowItemIds.SEND_TITLE

    override val nameTranslationKey = "action.sendTitle.name"
    override val detailTranslationKey = "action.sendTitle.detail"
    override val detailDefaultReplaces = listOf("player", "title", "subtitle", "fadeIn", "stay", "fadeOut")

    override val category = Category.PLAYER

    override var playerVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setPlayerVariableName(player)
    }

    override fun isDataValid(): Boolean {
        return getPlayerVariableName() != "" && (title != "" || subTitle != "")
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getPlayerVariableName(), title, subTitle, fadeIn, stay, fadeOut))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val title = source.replaceVariables(title)
        val subtitle = source.replaceVariables(subTitle)

        val fadeIn = source.replaceVariables(fadeIn).let {
            throwIfInvalidNumber(it)
            it.toInt()
        }
        val stay = source.replaceVariables(stay).let {
            throwIfInvalidNumber(it)
            it.toInt()
        }
        val fadeOut = source.replaceVariables(fadeOut).let {
            throwIfInvalidNumber(it)
            it.toInt()
        }

        val player = getPlayer(source)
        throwIfInvalidPlayer(player)

        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut)
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            PlayerVariableDropdown(variables, getPlayerVariableName()),
            ExampleInput("@action.sendTitle.form.title", "aieuo", title),
            ExampleInput("@action.sendTitle.form.subtitle", "aieuo", subTitle),
            ExampleNumberInput("@action.sendTitle.form.fadeIn", "-1", fadeIn, true, -1.0),
            ExampleNumberInput("@action.sendTitle.form.stay", "-1", stay, true, -1.0),
            ExampleNumberInput("@action.sendTitle.form.fadeOut", "-1", fadeOut, true, -1.0),
        )
    }

    override fun parseFromFormData(data: CustomFormResponseList): List<Any?> {
        if (data[1] == "" && data[2] == "") {
            throw InvalidFormValueException("@form.insufficient", 1)
        }
        return data
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setPlayerVariableName(contents.getString(0))
        title = contents.getString(1)
        subTitle = contents.getString(2)
        if (contents.size > 5) {
            fadeIn = contents.getString(3)
            stay = contents.getString(4)
            fadeOut = contents.getString(5)
        }
    }

    override fun serializeContents(): List<Any> {
        return listOf(title, subTitle, fadeIn, stay, fadeOut)
    }

    override fun clone(): SendTitle {
        val item = super.clone() as SendTitle
        item.playerVariableNames = playerVariableNames.toMutableMap()
        return item
    }
}
