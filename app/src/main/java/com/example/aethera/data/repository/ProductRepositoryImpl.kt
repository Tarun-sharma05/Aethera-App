package com.example.aethera.data.repository

import com.example.aethera.common.CATEGORY
import com.example.aethera.common.PRODUCTS
import com.example.aethera.common.ResultState
import com.example.aethera.domain.models.Category
import com.example.aethera.domain.models.Product
import com.example.aethera.domain.repository.ProductRepository
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ProductRepositoryImpl(
    private val db: FirebaseFirestore
) : ProductRepository {

    override fun getProducts(): Flow<ResultState<List<Product>>> = callbackFlow {
        Log.d(TAG, "getProducts() → attaching Firestore snapshot listener")
        trySend(ResultState.Loading)
        val listener = db.collection(PRODUCTS)
            .whereEqualTo("isActive", true)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    Log.e(TAG, "getProducts() → Firestore error: ${err.message}", err)
                    trySend(ResultState.Error(err.message ?: "Unknown error"))
                    return@addSnapshotListener
                }
                val products = snap?.documents?.mapNotNull { it.toObject(Product::class.java)?.copy(id = it.id) } ?: emptyList()
                Log.d(TAG, "getProducts() → snapshot received: ${products.size} active product(s)")
                products.forEach { p -> Log.v(TAG, "  product → id=${p.id}, name=${p.name}, category=${p.categoryName}, price=${p.price}") }
                trySend(ResultState.Success(products))
            }
        awaitClose {
            Log.d(TAG, "getProducts() → removing Firestore listener")
            listener.remove()
        }
    }

    override fun getProductById(productId: String): Flow<ResultState<Product>> = callbackFlow {
        Log.d(TAG, "getProductById() → fetching productId=$productId")
        trySend(ResultState.Loading)
        val listener = db.collection(PRODUCTS).document(productId)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    Log.e(TAG, "getProductById() → error for id=$productId: ${err.message}", err)
                    trySend(ResultState.Error(err.message ?: "Unknown error"))
                    return@addSnapshotListener
                }
                val product = snap?.toObject(Product::class.java)?.copy(id = snap.id)
                if (product != null) {
                    Log.d(TAG, "getProductById() → found: name=${product.name}, category=${product.categoryName}")
                    trySend(ResultState.Success(product))
                } else {
                    Log.w(TAG, "getProductById() → no document found for id=$productId")
                    trySend(ResultState.Error("Product not found"))
                }
            }
        awaitClose {
            Log.d(TAG, "getProductById() → removing listener for id=$productId")
            listener.remove()
        }
    }

    override fun getCategories(): Flow<ResultState<List<Category>>> = callbackFlow {
        Log.d(TAG, "getCategories() → attaching Firestore snapshot listener")
        trySend(ResultState.Loading)
        val listener = db.collection(CATEGORY)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    Log.e(TAG, "getCategories() → Firestore error: ${err.message}", err)
                    trySend(ResultState.Error(err.message ?: "Unknown error"))
                    return@addSnapshotListener
                }
                val cats = snap?.documents?.mapNotNull { it.toObject(Category::class.java)?.copy(id = it.id) } ?: emptyList()
                Log.d(TAG, "getCategories() → snapshot received: ${cats.size} category/ies")
                cats.forEach { c -> Log.v(TAG, "  category → id=${c.id}, name=${c.name}") }
                trySend(ResultState.Success(cats))
            }
        awaitClose {
            Log.d(TAG, "getCategories() → removing Firestore listener")
            listener.remove()
        }
    }

    override fun getProductsByCategory(categoryName: String): Flow<ResultState<List<Product>>> = callbackFlow {
        Log.d(TAG, "getProductsByCategory() → category='$categoryName'")
        trySend(ResultState.Loading)
        val listener = db.collection(PRODUCTS)
            .whereEqualTo("categoryName", categoryName)
            .whereEqualTo("isActive", true)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    Log.e(TAG, "getProductsByCategory() → error for '$categoryName': ${err.message}", err)
                    trySend(ResultState.Error(err.message ?: "Unknown error"))
                    return@addSnapshotListener
                }
                val products = snap?.documents?.mapNotNull { it.toObject(Product::class.java)?.copy(id = it.id) } ?: emptyList()
                Log.d(TAG, "getProductsByCategory() → '$categoryName': ${products.size} product(s) found")
                products.forEach { p -> Log.v(TAG, "  product → id=${p.id}, name=${p.name}") }
                trySend(ResultState.Success(products))
            }
        awaitClose {
            Log.d(TAG, "getProductsByCategory() → removing listener for '$categoryName'")
            listener.remove()
        }
    }

    companion object {
        private const val TAG = "ProductRepo"
    }
}
