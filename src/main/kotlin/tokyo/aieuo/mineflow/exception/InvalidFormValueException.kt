package tokyo.aieuo.mineflow.exception

class InvalidFormValueException(val errorMessage: String, val index: Int) : RuntimeException(errorMessage)