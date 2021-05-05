package tokyo.aieuo.mineflow.exception

import java.lang.RuntimeException

class InvalidFormValueException(val errorMessage: String, val index: Int) : RuntimeException(errorMessage)