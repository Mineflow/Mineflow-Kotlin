package tokyo.aieuo.mineflow.flowItem.action.player

import cn.nukkit.network.protocol.PlaySoundPacket
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
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.Language

class PlaySound(player: String = "", var sound: String = "", var volume: String = "1", var pitch: String = "1") :
    FlowItem(), PlayerFlowItem {

    override val id = FlowItemIds.PLAY_SOUND

    override val nameTranslationKey = "action.playSound.name"
    override val detailTranslationKey = "action.playSound.detail"
    override val detailDefaultReplaces = listOf("player", "sound", "volume", "pitch")

    override val category = Category.PLAYER

    override var playerVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setPlayerVariableName(player)
    }

    override fun isDataValid(): Boolean {
        return getPlayerVariableName() != "" && sound != "" && volume != "" && pitch != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getPlayerVariableName(), sound, volume, pitch))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        sound = source.replaceVariables(sound)
        volume = source.replaceVariables(volume)
        val pitch = source.replaceVariables(pitch)

        throwIfInvalidNumber(volume)
        throwIfInvalidNumber(pitch)

        val player = getPlayer(source)
        throwIfInvalidPlayer(player)

        val pk = PlaySoundPacket()
        pk.name = sound
        pk.x = player.x.toInt()
        pk.y = player.y.toInt()
        pk.z = player.z.toInt()
        pk.volume = volume.toFloat()
        pk.pitch = pitch.toFloat()
        player.dataPacket(pk)
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            PlayerVariableDropdown(variables, getPlayerVariableName()),
            ExampleInput("@action.playSound.form.sound", "random.levelup", sound, true),
            ExampleNumberInput("@action.playSound.form.volume", "1", volume, true),
            ExampleNumberInput("@action.playSound.form.pitch", "1", pitch, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setPlayerVariableName(contents.getString(0))
        sound = contents.getString(1)
        volume = contents.getString(2)
        pitch = contents.getString(3)
    }

    override fun serializeContents(): List<Any> {
        return listOf(getPlayerVariableName(), sound, volume, pitch)
    }

    override fun clone(): PlaySound {
        val item = super.clone() as PlaySound
        item.playerVariableNames = playerVariableNames.toMutableMap()
        return item
    }
}