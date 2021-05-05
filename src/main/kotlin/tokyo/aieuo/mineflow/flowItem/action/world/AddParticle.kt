package tokyo.aieuo.mineflow.flowItem.action.world

import cn.nukkit.network.protocol.SpawnParticleEffectPacket
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

class AddParticle(position: String = "", var particle: String = "", var amount: String = "1"): FlowItem(), PositionFlowItem {

    override val id = FlowItemIds.ADD_PARTICLE

    override val nameTranslationKey = "action.addParticle.name"
    override val detailTranslationKey = "action.addParticle.detail"
    override val detailDefaultReplaces = listOf("position", "particle", "amount", "")

    override val category = Category.WORLD

    override val permission = PERMISSION_LEVEL_1

    override var positionVariableNames: MutableMap<String, String> = mutableMapOf()

    init {
        setPositionVariableName(position)
    }

    override fun isDataValid(): Boolean {
        return getPositionVariableName() != "" && particle != "" && amount != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(getPositionVariableName(), particle, amount, if (amount == "1") "" else "s"))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val particleName = source.replaceVariables(particle)
        val amount = source.replaceVariables(amount)

        throwIfInvalidNumber(amount, 1.0)

        val position = getPosition(source)

        for (i in 1..amount.toInt()) {
            val pk = SpawnParticleEffectPacket()
            pk.position = position.asVector3f()
            pk.identifier = particleName

            position.level.players.forEach { (_, player) ->
                player.dataPacket(pk)
            }
        }
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: Map<String, DummyVariable<DummyVariable.Type>>): List<Element> {
        return listOf(
            PositionVariableDropdown(variables, getPositionVariableName()),
            ExampleInput("@action.addParticle.form.particle", "minecraft:explosion_particle", particle, true),
            ExampleNumberInput("@action.addParticle.form.amount", "1", amount, true, 1.0),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        setPositionVariableName(contents.getString(0))
        particle = contents.getString(1)
        amount = contents.getString(2)
    }

    override fun serializeContents(): List<Any> {
        return listOf(getPositionVariableName(), particle, amount)
    }

    override fun clone(): AddParticle {
        val item = super.clone() as AddParticle
        item.positionVariableNames = positionVariableNames.toMutableMap()
        return item
    }
}