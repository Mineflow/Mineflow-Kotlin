package tokyo.aieuo.mineflow.ui

import cn.nukkit.Player
import tokyo.aieuo.mineflow.formAPI.ListForm
import tokyo.aieuo.mineflow.formAPI.element.Button
import tokyo.aieuo.mineflow.ui.customForm.CustomFormForm
import tokyo.aieuo.mineflow.ui.trigger.EventTriggerForm

object HomeForm {

    fun sendMenu(player: Player) {
        (ListForm("@form.home.title"))
            .addButtons(
                Button("@mineflow.recipe") { RecipeForm.sendMenu(player) },
                Button("@mineflow.command") { CommandForm.sendMenu(player) },
                Button("@mineflow.event") { EventTriggerForm.sendSelectEvent(player) },
                Button("@mineflow.form") { CustomFormForm.sendMenu(player) },
                Button("@mineflow.settings") { SettingForm.sendMenu(player) },
                Button("@form.exit"),
            ).show(player)
    }
}