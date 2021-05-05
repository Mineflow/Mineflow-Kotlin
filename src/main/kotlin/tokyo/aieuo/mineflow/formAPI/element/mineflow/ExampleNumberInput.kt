package tokyo.aieuo.mineflow.formAPI.element.mineflow

import tokyo.aieuo.mineflow.formAPI.element.NumberInput
import tokyo.aieuo.mineflow.utils.Language

class ExampleNumberInput(
    text: String,
    example: String = "",
    default: String = "",
    required: Boolean = false,
    min: Double? = null,
    max: Double? = null,
    excludes: List<Double> = listOf()
) : NumberInput(
    text,
    Language.get("form.example", listOf(example)),
    default,
    required,
    min,
    max,
    excludes
)