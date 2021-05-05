package tokyo.aieuo.mineflow.ui

import cn.nukkit.Player
import tokyo.aieuo.mineflow.Main
import tokyo.aieuo.mineflow.formAPI.CustomForm
import tokyo.aieuo.mineflow.formAPI.ModalForm
import tokyo.aieuo.mineflow.formAPI.element.CancelToggle
import tokyo.aieuo.mineflow.formAPI.element.Input
import tokyo.aieuo.mineflow.recipe.Recipe
import tokyo.aieuo.mineflow.utils.Language

object MineflowForm {

    fun confirmRename(player: Player, name: String, newName: String, onAccept: (String) -> Unit, onRefuse: (String) -> Unit) {
        (ModalForm("@form.home.rename.title"))
            .setContent(Language.get("form.home.rename.content", listOf(name, newName)))
            .setButton1("@form.yes")
            .setButton2("@form.no")
            .onReceive { data -> if (data) onAccept(newName) else onRefuse(name) }
            .show(player)
    }

    fun selectRecipe(player: Player, title: String, callback: (Recipe) -> Unit, onCancel: (() -> Unit)? = null, defaultName: String = "", defaultGroup: String = "") {
        (CustomForm(title)).setContents(mutableListOf(
                Input("@form.recipe.recipeName", default = defaultName, required = true),
                Input("@form.recipe.groupName", default = defaultGroup),
                CancelToggle(onCancel),
            )).onReceive { data ->
                val manager = Main.recipeManager

                val inputName = data.getString(0)
                val inputGroup = data.getString(1)

                val (name, group) = if (inputGroup == "") {
                    manager.parseName(inputName)
                } else {
                    inputName to inputGroup
                }
                if (!manager.exists(name, group)) {
                    resend("@form.recipe.select.notfound" to 0)
                    return@onReceive
                }

                val recipe = manager.get(name, group)
                if (recipe === null) {
                    resend("@form.recipe.select.notfound" to 0)
                    return@onReceive
                }

                callback(recipe)
            }.show(player)
    }
}