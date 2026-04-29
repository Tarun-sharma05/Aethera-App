package com.example.aethera.domain.models

/** Single item in the CART/{userId}/items sub-collection */
data class CartItem(
    val productId : String = "",
    val name      : String = "",
    val imageUrl  : String = "",
    val price     : Double = 0.0,
    val quantity  : Int    = 1,
) {
    val totalPrice: Double get() = price * quantity
}
