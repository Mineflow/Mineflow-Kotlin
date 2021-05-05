package tokyo.aieuo.mineflow.flowItem.base


import cn.nukkit.Player
import cn.nukkit.entity.Entity
import tokyo.aieuo.mineflow.exception.InvalidFlowValueException
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.obj.EntityObjectVariable

interface EntityFlowItem {

    var entityVariableNames: MutableMap<String, String>

    fun getEntityVariableName(name: String = ""): String {
        return entityVariableNames[name] ?: ""
    }

    fun setEntityVariableName(entity: String, name: String = "") {
        entityVariableNames[name] = entity
    }

    fun getEntity(source: FlowItemExecutor, name: String = ""): Entity {
        val rawName = getEntityVariableName(name)
        val entity = source.replaceVariables(rawName)

        val variable = source.getVariable(entity)
        if (variable is EntityObjectVariable<*>) {
            return variable.value
        }

        throw InvalidFlowValueException(Language.get("action.target.not.valid", listOf(
            Language.get("action.target.require.entity"),
            rawName
        )))
    }

    fun throwIfInvalidEntity(entity: Entity, checkOnline: Boolean = true) {
        if (entity is Player && checkOnline && !entity.isOnline) {
            throw InvalidFlowValueException(Language.get("action.error.player.offline"))
        }
    }
}