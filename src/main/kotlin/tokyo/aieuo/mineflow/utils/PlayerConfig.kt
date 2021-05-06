package tokyo.aieuo.mineflow.utils

import cn.nukkit.utils.Config

class PlayerConfig(file: String) : Config(file) {

    fun getFavorites(name: String, type: String): MutableList<String> {
        return get("$name.favorite.$type", mutableListOf())
    }

    fun setFavorites(name: String, type: String, favorites: List<String>) {
        set("$name.favorite.$type", favorites)
    }

    fun addFavorite(name: String, type: String, favorite: String) {
        val favorites = getFavorites(name, type)
        if (favorite !in favorites) {
            favorites.add(favorite)
        }
        setFavorites(name, type, favorites)
    }

    fun removeFavorite(name: String, type: String, favorite: String) {
        val favorites = getFavorites(name, type)
        favorites.remove(favorite)
        setFavorites(name, type, favorites)
    }

    fun toggleFavorite(name: String, type: String, favorite: String) {
        val favorites = getFavorites(name, type)
        if (favorite in favorites) {
            removeFavorite(name, type, favorite)
        } else {
            addFavorite(name, type, favorite)
        }
    }
}