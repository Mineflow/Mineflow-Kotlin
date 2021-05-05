package tokyo.aieuo.mineflow.utils

import cn.nukkit.Player

class Session {

    companion object {
        val sessions = mutableMapOf<String, Session>()

        fun existsSession(player: Player): Boolean {
            return sessions.containsKey(player.name)
        }

        fun getSession(player: Player): Session {
            return sessions[player.name] ?: createSession(player)
        }

        fun createSession(player: Player): Session {
            val session = Session()
            sessions[player.name] = session
            return session
        }

        fun destroySession(player: Player) {
            sessions.remove(player.name)
        }
    }

    private val data = mutableMapOf<String, Any>()

    fun exists(key: String): Boolean {
        return data.containsKey(key)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String, default: T? = null): T? {
        if (!exists(key)) return default
        return data[key] as? T
    }

    fun set(key: String, value: Any) {
        data[key] = value
    }

    fun existsInt(key: String): Boolean {
        return exists(key) && get<Int>(key) is Int
    }
    fun getInt(key: String, default: Int = 0): Int {
        return get(key, default) ?: default
    }

    fun existsDouble(key: String): Boolean {
        return exists(key) && get<Double>(key) is Double
    }
    fun getDouble(key: String, default: Double = 0.0): Double {
        return get(key, default) ?: default
    }
    fun existsString(key: String): Boolean {
        return exists(key) && get<String>(key) is String
    }
    fun getString(key: String, default: String = ""): String {
        return get(key, default) ?: default
    }

    fun existsMap(key: String): Boolean {
        return exists(key) && get<Map<*, *>>(key) is Map<*, *>
    }
    fun <K, V> getMap(key: String, default: Map<K, V> = mapOf()): Map<K, V> {
        return get(key, default) ?: default
    }

    fun existsList(key: String): Boolean {
        return exists(key) && get<List<*>>(key) is List<*>
    }
    fun <T> getList(key: String, default: List<T> = listOf()): List<T> {
        return get(key, default) ?: default
    }

    fun existsDeque(key: String): Boolean {
        return exists(key) && get<ArrayDeque<*>>(key) is ArrayDeque<*>
    }
    fun <T> getDeque(key: String, default: ArrayDeque<T> = ArrayDeque()): ArrayDeque<T> {
        return get(key, default) ?: default
    }

    fun <T> getObject(key: String): T? {
        return get<T>(key)
    }

    fun remove(key: String) {
        data.remove(key)
    }
    fun remove(vararg key: String) {
        key.forEach { data.remove(it) }
    }

    fun removeAll() {
        data.clear()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> push(key: String, value: T): Boolean {
        val data = get<ArrayDeque<T>>(key) ?: return false

        data.addLast(value)
        return true
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> pop(key: String): T? {
        val data = get<ArrayDeque<T>>(key) ?: return null

        return data.removeLastOrNull()
    }
}