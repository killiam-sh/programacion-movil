package com.stocksync.error404

data class FavoriteItem(
    val title: String,
    val price: String,
    val description: String
)

object FavoritesRepository {
    val items = mutableListOf<FavoriteItem>()

    fun addItem(item: FavoriteItem) {
        if (!items.any { it.title == item.title }) {
            items.add(item)
        }
    }

    fun removeItem(item: FavoriteItem) {
        items.remove(item)
    }

    fun clear() {
        items.clear()
    }
}
