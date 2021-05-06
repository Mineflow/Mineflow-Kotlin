package tokyo.aieuo.mineflow.exception

class UndefinedMineflowPropertyException(variableName: String, propertyName: String) :
    Exception("§cUndefined index: ${variableName}.§l${propertyName}§r")