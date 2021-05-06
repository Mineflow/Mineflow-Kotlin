package tokyo.aieuo.mineflow.flowItem.action.entity

import cn.nukkit.Player
import cn.nukkit.entity.EntityHuman
import tokyo.aieuo.mineflow.exception.InvalidFlowValueException
import tokyo.aieuo.mineflow.flowItem.FlowItem
import tokyo.aieuo.mineflow.flowItem.FlowItemExecutor
import tokyo.aieuo.mineflow.flowItem.FlowItemIds
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.mineflow.ExampleInput
import tokyo.aieuo.mineflow.formAPI.response.CustomFormResponseList
import tokyo.aieuo.mineflow.utils.Category
import tokyo.aieuo.mineflow.utils.DummyVariableMap
import tokyo.aieuo.mineflow.utils.EntityHolder
import tokyo.aieuo.mineflow.utils.Language
import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.obj.EntityObjectVariable
import tokyo.aieuo.mineflow.variable.obj.HumanObjectVariable
import tokyo.aieuo.mineflow.variable.obj.PlayerObjectVariable

class GetEntity(var entityId: String = "", var resultName: String = "entity") : FlowItem() {

    override val id = FlowItemIds.GET_ENTITY

    override val nameTranslationKey = "action.getEntity.name"
    override val detailTranslationKey = "action.getEntity.detail"
    override val detailDefaultReplaces = listOf("id", "result")

    override val category = Category.ENTITY

    override fun isDataValid(): Boolean {
        return entityId != "" && resultName != ""
    }

    override fun getDetail(): String {
        if (!isDataValid()) return getName()
        return Language.get(detailTranslationKey, listOf(entityId, resultName))
    }

    override fun execute(source: FlowItemExecutor) = sequence {
        throwIfCannotExecute()

        val id = source.replaceVariables(entityId)
        val resultName = source.replaceVariables(resultName)

        throwIfInvalidNumber(id, 0.0)

        val entity = EntityHolder.findEntity(id.toLong())
        if (entity === null) {
            throw InvalidFlowValueException(Language.get("action.getEntity.notFound", listOf(id)))
        }

        val variable = when (entity) {
            is Player -> PlayerObjectVariable(entity, entity.name)
            is EntityHuman -> HumanObjectVariable(entity, entity.nameTag)
            else -> EntityObjectVariable(entity, entity.nameTag)
        }
        source.addVariable(resultName, variable)
        yield(FlowItemExecutor.Result.CONTINUE)
    }

    override fun getEditFormElements(variables: DummyVariableMap): List<Element> {
        return listOf(
            ExampleInput("@action.getEntity.form.target", "aieuo", entityId, true),
            ExampleInput("@action.form.resultVariableName", "entity", resultName, true),
        )
    }

    override fun loadSaveData(contents: CustomFormResponseList) = apply {
        entityId = contents.getString(0)
        resultName = contents.getString(1)
    }

    override fun serializeContents(): List<Any> {
        return listOf(entityId, resultName)
    }

    override fun getAddingVariables(): DummyVariableMap {
        return mapOf(
            resultName to DummyVariable(DummyVariable.Type.PLAYER, entityId)
        )
    }
}