package com.example.aethera.data.repository

import com.example.aethera.common.CATEGORY
import com.example.aethera.common.PRODUCTS
import com.example.aethera.common.ResultState
import com.example.aethera.domain.models.Category
import com.example.aethera.domain.models.Product
import com.example.aethera.domain.repository.ProductRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ProductRepositoryImpl(
    private val db: FirebaseFirestore
) : ProductRepository {

    override fun getProducts(): Flow<ResultState<List<Product>>> = callbackFlow {
        trySend(ResultState.Loading)
        val listener = db.collection(PRODUCTS)
            .whereEqualTo("isActive", true)
            .addSnapshotListener { snap, err ->
                if (err != null) { trySend(ResultState.Error(err.message ?: "Unknown error")); return@addSnapshotListener }
                val products = snap?.documents?.mapNotNull { it.toObject(Product::class.java)?.copy(id = it.id) } ?: emptyList()
                trySend(ResultState.Success(products))
            }
        awaitClose { listener.remove() }
    }

    override fun getProductById(productId: String): Flow<ResultState<Product>> = callbackFlow {
        trySend(ResultState.Loading)
        val listener = db.collection(PRODUCTS).document(productId)
            .addSnapshotListener { snap, err ->
                if (err != null) { trySend(ResultState.Error(err.message ?: "Unknown error")); return@addSnapshotListener }
                val product = snap?.toObject(Product::class.java)?.copy(id = snap.id)
                if (product != null) trySend(ResultState.Success(product))
                else trySend(ResultState.Error("Product not found"))
            }
        awaitClose { listener.remove() }
    }

    override fun getCategories(): Flow<ResultState<List<Category>>> = callbackFlow {
        trySend(ResultState.Loading)
        val listener = db.collection(CATEGORY)
            .addSnapshotListener { snap, err ->
                if (err != null) { trySend(ResultState.Error(err.message ?: "Unknown error")); return@addSnapshotListener }
                val cats = snap?.documents?.mapNotNull { it.toObject(Category::class.java)?.copy(id = it.id) } ?: emptyList()
                trySend(ResultState.Success(cats))
            }
        awaitClose { listener.remove() }
    }

    override fun getProductsByCategory(categoryName: String): Flow<ResultState<List<Product>>> = callbackFlow {
        trySend(ResultState.Loading)
        val listener = db.collection(PRODUCTS)
            .whereEqualTo("categoryName", categoryName)
            .whereEqualTo("isActive", true)
            .addSnapshotListener { snap, err ->
                if (err != null) { trySend(ResultState.Error(err.message ?: "Unknown error")); return@addSnapshotListener }
                val products = snap?.documents?.mapNotNull { it.toObject(Product::class.java)?.copy(id = it.id) } ?: emptyList()
                trySend(ResultState.Success(products))
            }
        awaitClose { listener.remove() }
    }
}
