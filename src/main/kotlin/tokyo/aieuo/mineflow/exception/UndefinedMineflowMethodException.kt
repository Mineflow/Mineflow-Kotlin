package tokyo.aieuo.mineflow.exception

class UndefinedMineflowMethodException(variableName: String, methodName: String)
    : Exception("§cUndefined method: ${variableName}§l${methodName}()§r")