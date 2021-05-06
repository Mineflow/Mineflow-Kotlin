package tokyo.aieuo.mineflow.formAPI.element.mineflow

import tokyo.aieuo.mineflow.formAPI.element.Input
import tokyo.aieuo.mineflow.utils.Language

class ExampleInput(text: String, example: String = "", default: String = "", required: Boolean = false) :
    Input(text, Language.get("form.example", listOf(example)), default, required)