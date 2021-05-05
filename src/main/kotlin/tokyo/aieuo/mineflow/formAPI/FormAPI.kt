package tokyo.aieuo.mineflow.formAPI

import cn.nukkit.Player
import cn.nukkit.network.protocol.ModalFormRequestPacket
import com.fasterxml.jackson.databind.ObjectMapper
import tokyo.aieuo.mineflow.utils.json_encode

object FormAPI {

    val formIds: MutableMap<String, Int> = mutableMapOf()
    val forms: MutableMap<String, MutableMap<Int, Form>> = mutableMapOf()

    fun getFormId(player: Player): Int {
        val count = formIds.getOrPut(player.name) { 100000000 }
        formIds[player.name] = count + 1
        return count
    }

    fun Player.sendForm(form: Form): Int {
        val id = getFormId(this)
        val pk = ModalFormRequestPacket()
        pk.formId = id
        pk.data = json_encode(form)
        dataPacket(pk)

        val forms = forms.getOrPut(name) { mutableMapOf() }
        forms[id] = form
        return id
    }

    fun onReceiveForm(player: Player, formId: Int, formData: String) {
        val forms = forms[player.name] ?: return

        if (forms.containsKey(formId)) {
            val form = forms.remove(formId) ?: return

            if (formData == "null") {
                form.handleOnClose(player)
                return
            }

            when (form) {
                is ModalForm -> form.handleResponse(player, formData == "true")
                is ListForm -> form.handleResponse(player, formData.toInt())
                is CustomForm -> form.handleResponse(player, ObjectMapper().readValue(formData, mutableListOf<Any?>().javaClass))
            }
        }
    }

}

typealias FormError = Pair<String, Int>