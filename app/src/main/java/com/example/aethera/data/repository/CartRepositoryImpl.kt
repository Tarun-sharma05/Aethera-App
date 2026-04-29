package com.example.aethera.data.repository

import com.example.aethera.common.CART
import com.example.aethera.common.ITEMS
import com.example.aethera.common.ResultState
import com.example.aethera.domain.models.CartItem
import com.example.aethera.domain.repository.CartRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class CartRepositoryImpl(
    private val db: FirebaseFirestore
) : CartRepository {

    private fun itemsRef(userId: String) =
        db.collection(CART).document(userId).collection(ITEMS)

    override fun getCartItems(userId: String): Flow<ResultState<List<CartItem>>> = callbackFlow {
        trySend(ResultState.Loading)
        val listener = itemsRef(userId).addSnapshotListener { snap, err ->
            if (err != null) { trySend(ResultState.Error(err.message ?: "Error")); return@addSnapshotListener }
            val items = snap?.documents?.mapNotNull { it.toObject(CartItem::class.java) } ?: emptyList()
            trySend(ResultState.Success(items))
        }
        awaitClose { listener.remove() }
    }

    override fun addToCart(userId: String, item: CartItem): Flow<ResultState<Unit>> = callbackFlow {
        trySend(ResultState.Loading)
        itemsRef(userId).document(item.productId).set(item)
            .addOnSuccessListener { trySend(ResultState.Success(Unit)) }
            .addOnFailureListener { trySend(ResultState.Error(it.message ?: "Error")) }
        awaitClose()
    }

    override fun removeFromCart(userId: String, productId: String): Flow<ResultState<Unit>> = callbackFlow {
        trySend(ResultState.Loading)
        itemsRef(userId).document(productId).delete()
            .addOnSuccessListener { trySend(ResultState.Success(Unit)) }
            .addOnFailureListener { trySend(ResultState.Error(it.message ?: "Error")) }
        awaitClose()
    }

    override fun updateQuantity(userId: String, productId: String, quantity: Int): Flow<ResultState<Unit>> = callbackFlow {
        trySend(ResultState.Loading)
        itemsRef(userId).document(productId).update("quantity", quantity)
            .addOnSuccessListener { trySend(ResultState.Success(Unit)) }
            .addOnFailureListener { trySend(ResultState.Error(it.message ?: "Error")) }
        awaitClose()
    }

    override fun clearCart(userId: String): Flow<ResultState<Unit>> = callbackFlow {
        trySend(ResultState.Loading)
        itemsRef(userId).get()
            .addOnSuccessListener { snap ->
                val batch = db.batch()
                snap.documents.forEach { batch.delete(it.reference) }
                batch.commit()
                    .addOnSuccessListener { trySend(ResultState.Success(Unit)) }
                    .addOnFailureListener { trySend(ResultState.Error(it.message ?: "Error")) }
            }
            .addOnFailureListener { trySend(ResultState.Error(it.message ?: "Error")) }
        awaitClose()
    }
}
