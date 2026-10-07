package com.stocksync.error404

import java.util.UUID

data class FavoriteItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val price: String,
    val description: String,
    val imageUrl: String = ""
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as FavoriteItem
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

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
