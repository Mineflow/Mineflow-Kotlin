package tokyo.aieuo.mineflow.ui

import cn.nukkit.Player
import cn.nukkit.Server
import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.formAPI.CustomForm
import tokyo.aieuo.mineflow.formAPI.ListForm
import tokyo.aieuo.mineflow.formAPI.element.Button
import tokyo.aieuo.mineflow.formAPI.element.Dropdown
import tokyo.aieuo.mineflow.formAPI.element.Element
import tokyo.aieuo.mineflow.formAPI.element.Toggle
import tokyo.aieuo.mineflow.trigger.event.EventTrigger
import tokyo.aieuo.mineflow.utils.Language

object SettingForm {
    fun sendMenu(player: Player, messages: List<String> = listOf()) {
        (ListForm("@mineflow.settings"))
            .addButtons(
                Button("@form.back") { HomeForm.sendMenu(player) },
                Button("@setting.language") { selectLanguageForm(player) },
                Button("@setting.event") { sendEventListForm(player) },
            ).addMessages(messages).show(player)
    }

    fun selectLanguageForm(player: Player) {
        val languages = Language.getAvailableLanguages()
        (CustomForm("@setting.language"))
            .setContents(
                mutableListOf(
                    Dropdown(
                        "@setting.language",
                        languages.toList(),
                        languages.indexOf(Language.language)
                    ),
                )
            ).onReceive { data ->
                val language = languages[data.getInt(0)]
                Server.getInstance().dispatchCommand(player, "mineflow language $language")
            }.show(player)
    }

    fun sendEventListForm(player: Player) {
        val events = Main.eventManager.events
        val contents = mutableListOf<Element>()
        for ((name, enabled) in events) {
            contents.add(Toggle(EventTrigger.create(name).toString(), enabled))
        }
        (CustomForm("@setting.event"))
            .setContents(contents)
            .onReceive { data ->
                var count = 0
                for ((name, enabled) in events) {
                    if (data.getBoolean(count) && !enabled) {
                        Main.eventManager.enableEvent(name)
                    } else if (!data.getBoolean(count) && enabled) {
                        Main.eventManager.disableEvent(name)
                    }
                    count++
                }
                sendMenu(player, listOf("@setting.event.changed"))
            }.show(player)
    }
}