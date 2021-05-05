package tokyo.aieuo.mineflow.trigger.block

import tokyo.aieuo.mineflow.variable.Variable
import tokyo.aieuo.mineflow.trigger.Trigger
import tokyo.aieuo.mineflow.trigger.Triggers
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DefaultVariables
import tokyo.aieuo.mineflow.variable.DummyVariable
import cn.nukkit.block.Block

class BlockTrigger(key: String, subKey: String = ""): Trigger(Triggers.BLOCK, key, subKey) {

    companion object {
        fun create(key: String, subKey: String = ""): BlockTrigger {
            return BlockTrigger(key, subKey)
        }
    }

    fun getVariables(block: Block): Map<String, Variable<Any>> {
        return DefaultVariables.getBlockVariables(block)
    }

    override fun getVariablesDummy(): Map<String, DummyVariable<DummyVariable.Type>> {
        return mapOf(
            "block" to DummyVariable(DummyVariable.Type.BLOCK)
        )
    }

    override fun toString(): String {
        return Language.get("trigger.block.string", listOf(key))
    }
}