package com.example.aethera.data.repository

import com.example.aethera.common.ORDERS
import com.example.aethera.common.ResultState
import com.example.aethera.domain.models.Order
import com.example.aethera.domain.repository.OrderRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class OrderRepositoryImpl(
    private val db: FirebaseFirestore
) : OrderRepository {

    override fun placeOrder(order: Order): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        val docRef = db.collection(ORDERS).document()
        val orderWithId = order.copy(orderId = docRef.id, createdAt = System.currentTimeMillis())
        docRef.set(orderWithId)
            .addOnSuccessListener { trySend(ResultState.Success(docRef.id)) }
            .addOnFailureListener { trySend(ResultState.Error(it.message ?: "Order placement failed")) }
        awaitClose()
    }

    override fun getOrders(userId: String): Flow<ResultState<List<Order>>> = callbackFlow {
        trySend(ResultState.Loading)
        val listener = db.collection(ORDERS)
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) { trySend(ResultState.Error(err.message ?: "Error")); return@addSnapshotListener }
                val orders = snap?.documents?.mapNotNull { it.toObject(Order::class.java) } ?: emptyList()
                trySend(ResultState.Success(orders))
            }
        awaitClose { listener.remove() }
    }

    override fun getOrderById(orderId: String): Flow<ResultState<Order>> = callbackFlow {
        trySend(ResultState.Loading)
        val listener = db.collection(ORDERS).document(orderId)
            .addSnapshotListener { snap, err ->
                if (err != null) { trySend(ResultState.Error(err.message ?: "Error")); return@addSnapshotListener }
                val order = snap?.toObject(Order::class.java)
                if (order != null) trySend(ResultState.Success(order))
                else trySend(ResultState.Error("Order not found"))
            }
        awaitClose { listener.remove() }
    }
}
