package com.example.aethera.domain.repository

import com.example.aethera.common.ResultState
import com.example.aethera.domain.models.Category
import com.example.aethera.domain.models.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(): Flow<ResultState<List<Product>>>
    fun getProductById(productId: String): Flow<ResultState<Product>>
    fun getCategories(): Flow<ResultState<List<Category>>>
    fun getProductsByCategory(categoryName: String): Flow<ResultState<List<Product>>>
}
