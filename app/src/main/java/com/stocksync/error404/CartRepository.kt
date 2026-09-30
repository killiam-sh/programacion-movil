package com.stocksync.error404

data class CartItem(
    val title: String,
    val price: String,
    val description: String,
    var isSelected: Boolean = true
)

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
