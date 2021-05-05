package tokyo.aieuo.mineflow.exception

class UndefinedMineflowVariableException(val variableName: String)
    : Exception("§cUndefined variable: ${variableName}§r")