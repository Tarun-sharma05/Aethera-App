package com.example.aethera.data.repository

import com.example.aethera.common.ITEMS
import com.example.aethera.common.ResultState
import com.example.aethera.common.WISHLIST
import com.example.aethera.domain.repository.WishlistRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class WishlistRepositoryImpl(
    private val db: FirebaseFirestore
) : WishlistRepository {

    private fun itemsRef(userId: String) =
        db.collection(WISHLIST).document(userId).collection(ITEMS)

    override fun getWishlist(userId: String): Flow<ResultState<List<String>>> = callbackFlow {
        trySend(ResultState.Loading)
        val listener = itemsRef(userId).addSnapshotListener { snap, err ->
            if (err != null) { trySend(ResultState.Error(err.message ?: "Error")); return@addSnapshotListener }
            val ids = snap?.documents?.map { it.id } ?: emptyList()
            trySend(ResultState.Success(ids))
        }
        awaitClose { listener.remove() }
    }

    override fun addToWishlist(userId: String, productId: String): Flow<ResultState<Unit>> = callbackFlow {
        trySend(ResultState.Loading)
        itemsRef(userId).document(productId).set(mapOf("productId" to productId))
            .addOnSuccessListener { trySend(ResultState.Success(Unit)) }
            .addOnFailureListener { trySend(ResultState.Error(it.message ?: "Error")) }
        awaitClose()
    }

    override fun removeFromWishlist(userId: String, productId: String): Flow<ResultState<Unit>> = callbackFlow {
        trySend(ResultState.Loading)
        itemsRef(userId).document(productId).delete()
            .addOnSuccessListener { trySend(ResultState.Success(Unit)) }
            .addOnFailureListener { trySend(ResultState.Error(it.message ?: "Error")) }
        awaitClose()
    }

    override fun isInWishlist(userId: String, productId: String): Flow<ResultState<Boolean>> = callbackFlow {
        trySend(ResultState.Loading)
        val listener = itemsRef(userId).document(productId).addSnapshotListener { snap, err ->
            if (err != null) { trySend(ResultState.Error(err.message ?: "Error")); return@addSnapshotListener }
            trySend(ResultState.Success(snap?.exists() == true))
        }
        awaitClose { listener.remove() }
    }

    /** Fix #3: Returns a live count of wishlisted items for [userId] to power the ProfileScreen stats widget. */
    override fun getWishlistCount(userId: String): Flow<ResultState<Int>> = callbackFlow {
        trySend(ResultState.Loading)
        val listener = itemsRef(userId).addSnapshotListener { snap, err ->
            if (err != null) { trySend(ResultState.Error(err.message ?: "Error")); return@addSnapshotListener }
            trySend(ResultState.Success(snap?.size() ?: 0))
        }
        awaitClose { listener.remove() }
    }
}
