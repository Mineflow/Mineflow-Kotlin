package tokyo.aieuo.mineflow.flowItem.action.entity

import cn.nukkit.nbt.tag.*
import tokyo.aieuo.mineflow.entity.MineflowHuman
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.PlayerFlowItem
import tokyo.aieuo.mineflow.flowItem.base.PositionFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.element.mineflow.PositionVariableDropdown
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.obj.HumanObjectVariable
import java.nio.charset.StandardCharsets


class CreateHumanEntity(playerName: String = "", pos: String = "", var resultName: String = "human") : FlowItem(),
    PlayerFlowItem, PositionFlowItem {

    override val id = FlowItemIds.CREATE_HUMAN_ENTITY

    override val nameTranslationKey = "action.createHuman.name"
    override val detailTranslationKey = "action.createHuman.detail"
    override val detailDefaultReplaces = listOf("skin", "pos", "result")

    override val category = Category.ENTITY

    override var playerVariableNames: MutableMap<String, String> = mutableMapOf()
    override var positionVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setPlayerVariableName(playerName)
        setPositionVariableName(pos)
    }

    override fun isDataValid(): Boolean {
        return getPlayerVariableName() != "" && getPositionVariableName() != "" && resultName != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(
            detailTranslationKey,
            listOf(getPlayerVariableName(), getPositionVariableName(), resultName)
        )
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val player = getPlayer(source)
        throwIfInvalidPlayer(player)

        val pos = getPosition(source)

        val resultName = source.replaceVariables(resultName)

        val skin = player.skin
        val nbt = CompoundTag()
            .putList(
                ListTag<Tag>("Pos")
                    .add(DoubleTag("", pos.x))
                    .add(DoubleTag("", pos.y))
                    .add(DoubleTag("", pos.z))
            )
            .putList(
                ListTag<DoubleTag>("Motion")
                    .add(DoubleTag("", 0.0))
                    .add(DoubleTag("", 0.0))
                    .add(DoubleTag("", 0.0))
            )
            .putList(
                ListTag<FloatTag>("Rotation")
                    .add(FloatTag("", 0f))
                    .add(FloatTag("", 0f))
            )
            .putCompound(
                "Skin", CompoundTag()
                    .putByteArray("Data", skin.skinData.data)
                    .putInt("SkinImageWidth", skin.skinData.width)
                    .putInt("SkinImageHeight", skin.skinData.height)
                    .putString("ModelId", skin.skinId)
                    .putString("CapeId", skin.capeId)
                    .putByteArray("CapeData", skin.capeData.data)
                    .putInt("CapeImageWidth", skin.capeData.width)
                    .putInt("CapeImageHeight", skin.capeData.height)
                    .putByteArray("SkinResourcePatch", skin.skinResourcePatch.toByteArray(StandardCharsets.UTF_8))
                    .putByteArray("GeometryData", skin.geometryData.toByteArray(StandardCharsets.UTF_8))
                    .putByteArray("AnimationData", skin.animationData.toByteArray(StandardCharsets.UTF_8))
                    .putBoolean("PremiumSkin", skin.isPremium)
                    .putBoolean("PersonaSkin", skin.isPersona)
                    .putBoolean("CapeOnClassicSkin", skin.isCapeOnClassic)
            )

        val entity = MineflowHuman(pos.chunk, nbt)
        entity.spawnToAll()

        val variable = HumanObjectVariable(entity)
        source.addVariable(resultName, variable)
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            ExampleInput("@action.createHuman.form.skin", "target", getPlayerVariableName(), true),
            PositionVariableDropdown(variables, getPositionVariableName()),
            ExampleInput("@action.form.resultVariableName", "entity", resultName, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setPlayerVariableName(contents.getString(0))
        setPositionVariableName(contents.getString(1))
        resultName = contents.getString(2)
    }

    override fun serializeContents(): List<Any> {
        return listOf(getPlayerVariableName(), getPositionVariableName(), resultName)
    }

    override fun getAddingVariables(): DummyVariableMap {
        return mapOf(
            resultName to DummyVariable(DummyVariable.Type.ENTITY, getPlayerVariableName())
        )
    }

    override fun clone(): CreateHumanEntity {
        val item = super.clone() as CreateHumanEntity
        item.playerVariableNames = playerVariableNames.toMutableMap()
        item.positionVariableNames = positionVariableNames.toMutableMap()
        return item
    }
}