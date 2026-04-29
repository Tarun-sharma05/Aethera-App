package com.example.aethera.domain.repository

import com.example.aethera.common.ResultState
import com.example.aethera.domain.models.CartItem
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getCartItems(userId: String): Flow<ResultState<List<CartItem>>>
    fun addToCart(userId: String, item: CartItem): Flow<ResultState<Unit>>
    fun removeFromCart(userId: String, productId: String): Flow<ResultState<Unit>>
    fun updateQuantity(userId: String, productId: String, quantity: Int): Flow<ResultState<Unit>>
    fun clearCart(userId: String): Flow<ResultState<Unit>>
}
