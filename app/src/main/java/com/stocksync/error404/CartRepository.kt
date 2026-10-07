package com.stocksync.error404

import java.util.UUID

data class CartItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val price: String,
    val description: String,
    val imageUrl: String = "",
    var isSelected: Boolean = true
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as CartItem
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

object CartRepository {
    val items = mutableListOf<CartItem>()

    fun addItem(item: CartItem) {
        items.add(item)
    }

    fun removeItem(item: CartItem) {
        items.remove(item)
    }

    fun clearSelected() {
        items.removeAll { it.isSelected }
    }

    fun clearAll() {
        items.clear()
    }
}
