package tokyo.aieuo.mineflow.flowItem.action.item

import cn.nukkit.item.enchantment.Enchantment
import tokyo.aieuo.mineflow.exception.InvalidFlowValueException
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.ItemFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleNumberInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language

class AddEnchantment(item: String = "", var enchantId: String = "", var enchantLevel: String = "1") :
    FlowItem(), ItemFlowItem {

    override val id = FlowItemIds.ADD_ENCHANTMENT

    override val nameTranslationKey = "action.addEnchant.name"
    override val detailTranslationKey = "action.addEnchant.detail"
    override val detailDefaultReplaces = listOf("item", "id", "world")

    override val category = Category.ITEM

    override var itemVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setItemVariableName(item)
    }

    override fun isDataValid(): Boolean {
        return getItemVariableName() != "" && enchantId != "" && enchantLevel != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getItemVariableName(), enchantId, enchantLevel))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val item = getItem(source)

        val id = try {
            getEnchantIdByName(source.replaceVariables(enchantId))
        } catch (e: NumberFormatException) {
            throw InvalidFlowValueException(Language.get("action.addEnchant.enchant.notFound", listOf(id)))
        }
        val enchantment = Enchantment.getEnchantment(id)
            ?: throw InvalidFlowValueException(
                Language.get("action.addEnchant.enchant.notFound", listOf(id.toString()))
            )

        val level = source.replaceVariables(enchantLevel)
        throwIfInvalidNumber(level)
        enchantment.level = level.toInt()

        item.addEnchantment(enchantment)
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            ExampleInput("@action.form.target.item", "item", getItemVariableName(), true),
            ExampleInput("@action.addEnchant.form.id", "1", enchantId, true),
            ExampleNumberInput("@action.addEnchant.form.level", "1", enchantLevel, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setItemVariableName(contents.getString(0))
        enchantId = contents.getString(1)
        enchantLevel = contents.getString(2)
    }

    override fun serializeContents(): List<Any> {
        return listOf(getItemVariableName(), enchantId, enchantLevel)
    }

    fun getEnchantIdByName(value: String): Int {
        return when (value) {
            "protection" -> 0
            "fire_protection" -> 1
            "feather_falling" -> 2
            "blast_protection" -> 3
            "projectile_projection" -> 4
            "thorns" -> 5
            "respiration" -> 6
            "aqua_affinity" -> 7
            "depth_strider" -> 8
            "sharpness" -> 9
            "smite" -> 10
            "bane_of_arthropods" -> 11
            "knockback" -> 12
            "fire_aspect" -> 13
            "looting" -> 14
            "efficiency" -> 15
            "silk_touch" -> 16
            "durability" -> 17
            "fortune" -> 18
            "power" -> 19
            "punch" -> 20
            "flame" -> 21
            "infinity" -> 22
            "luck_of_the_sea" -> 23
            "lure" -> 24
            "frost_walker" -> 25
            "mending" -> 26
            "binding_curse" -> 27
            "vanishing_curse" -> 28
            "impaling" -> 29
            "loyality" -> 30
            "riptide" -> 31
            "channeling" -> 32
            "multishot" -> 33
            "piercing" -> 34
            "quick_charge" -> 35
            "soul_speed" -> 36
            else -> value.toInt()
        }
    }

    override fun clone(): AddEnchantment {
        val item = super.clone() as AddEnchantment
        item.itemVariableNames = itemVariableNames.toMutableMap()
        return item
    }
}
