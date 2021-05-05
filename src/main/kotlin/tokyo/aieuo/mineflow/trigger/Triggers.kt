package tokyo.aieuo.mineflow.trigger

import tokyo.aieuo.mineflow.trigger.block.BlockTrigger
import tokyo.aieuo.mineflow.trigger.command.CommandTrigger
import tokyo.aieuo.mineflow.trigger.custom.CustomTrigger
import tokyo.aieuo.mineflow.trigger.event.EventTrigger
import tokyo.aieuo.mineflow.trigger.form.FormTrigger
import tokyo.aieuo.mineflow.trigger.time.TimeTrigger
import tokyo.aieuo.mineflow.ui.trigger.*

object Triggers {

    const val BLOCK = "block"
    const val COMMAND = "command"
    const val EVENT = "event"
    const val FORM = "form"
    const val TIME = "time"
    const val CUSTOM = "custom"

    val forms: MutableMap<String, TriggerForm> = mutableMapOf()
    val list: MutableMap<String, (key: String, subKey: String) -> Trigger> = mutableMapOf()

    fun init() {
        add(BLOCK, BlockTriggerForm) { key, subKey -> BlockTrigger.create(key, subKey) }
        add(COMMAND, CommandTriggerForm) { key, subKey -> CommandTrigger.create(key, subKey) }
        add(EVENT, EventTriggerForm) { key, subKey -> EventTrigger.create(key, subKey) }
        add(FORM, FormTriggerForm) { key, subKey -> FormTrigger.create(key, subKey) }
        add(TIME, TimeTriggerForm) { key, subKey -> TimeTrigger.create(key, subKey) }
        add(CUSTOM, CustomTriggerForm) { key, subKey -> CustomTrigger.create(key, subKey) }
    }

    fun <T: Trigger> add(type: String, form: TriggerForm, creation: (String, String) -> T) {
        forms[type] = form
        list[type] = creation
    }

    fun getTrigger(type: String, key: String = "", subKey: String = ""): Trigger? {
        val creationFunc = list[type] ?: return null

        return creationFunc(key, subKey)
    }

    fun getAllForm() = forms

    fun getForm(type: String): TriggerForm? {
        return forms[type]
    }

    fun existsForm(type: String): Boolean {
        return forms.containsKey(type)
    }

}