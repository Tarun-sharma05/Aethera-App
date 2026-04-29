package com.example.aethera.domain.repository

import com.example.aethera.common.ResultState
import kotlinx.coroutines.flow.Flow

interface WishlistRepository {
    fun getWishlist(userId: String): Flow<ResultState<List<String>>>   // returns list of productIds
    fun addToWishlist(userId: String, productId: String): Flow<ResultState<Unit>>
    fun removeFromWishlist(userId: String, productId: String): Flow<ResultState<Unit>>
    fun isInWishlist(userId: String, productId: String): Flow<ResultState<Boolean>>
}
