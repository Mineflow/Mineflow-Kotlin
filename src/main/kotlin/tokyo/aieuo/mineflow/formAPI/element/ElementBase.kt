package tokyo.aieuo.mineflow.formAPI.element

import cn.nukkit.utils.TextFormat

abstract class ElementBase(text: String): Element, Cloneable {

    override var text: String = text.replace("\\n", "\n")
        set(value) {
            field = value.replace("\\n", "\n")
        }

    override var extraText: String = ""
    override var highlight: TextFormat? = null

    public override fun clone(): Element {
        return super.clone() as Element
    }
}