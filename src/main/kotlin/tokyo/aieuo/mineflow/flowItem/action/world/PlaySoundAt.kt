package tokyo.aieuo.mineflow.flowItem.action.world

import cn.nukkit.network.protocol.PlaySoundPacket
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.flowItem.base.PositionFlowItem
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleNumberInput
import tokyo.aieuo.mineflow.formAPI.element.mineflow.PositionVariableDropdown
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable

class PlaySoundAt(position: String = "", var sound: String = "", var volume: String = "1", var pitch: String = "1"): FlowItem(), PositionFlowItem {

    override val id = FlowItemIds.PLAY_SOUND_AT

    override val nameTranslationKey = "action.playSoundAt.name"
    override val detailTranslationKey = "action.playSoundAt.detail"
    override val detailDefaultReplaces = listOf("position", "sound", "volume", "pitch")

    override val category = Category.WORLD

    override var positionVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setPositionVariableName(position)
    }

    override fun isDataValid(): Boolean {
        return getPositionVariableName() != "" && sound != "" && volume != "" && pitch != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getPositionVariableName(), sound, volume, pitch))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val sound = source.replaceVariables(sound)
        val volume = source.replaceVariables(volume)
        val pitch = source.replaceVariables(pitch)

        throwIfInvalidNumber(volume)
        throwIfInvalidNumber(pitch)

        val position = getPosition(source)

        val pk = PlaySoundPacket()
        pk.name = sound
        pk.x = position.x.toInt()
        pk.y = position.y.toInt()
        pk.z = position.z.toInt()
        pk.volume = volume.toFloat()
        pk.pitch = pitch.toFloat()

        position.level.players.forEach { (_, player) -> player.dataPacket(pk) }
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            PositionVariableDropdown(variables, getPositionVariableName()),
            ExampleInput("@action.playSound.form.sound", "random.levelup", sound, true),
            ExampleNumberInput("@action.playSound.form.volume", "1", volume, true),
            ExampleNumberInput("@action.playSound.form.pitch", "1", pitch, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setPositionVariableName(contents.getString(0))
        sound = contents.getString(1)
        volume = contents.getString(2)
        pitch = contents.getString(3)
    }

    override fun serializeContents(): List<Any> {
        return listOf(getPositionVariableName(), sound, volume, pitch)
    }

    override fun clone(): PlaySoundAt {
        val item = super.clone() as PlaySoundAt
        item.positionVariableNames = positionVariableNames.toMutableMap()
        return item
    }
}