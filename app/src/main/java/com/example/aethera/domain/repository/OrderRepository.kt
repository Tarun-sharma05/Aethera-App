package com.example.aethera.domain.repository

import com.example.aethera.common.ResultState
import com.example.aethera.domain.models.Order
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    fun placeOrder(order: Order): Flow<ResultState<String>>   // returns orderId
    fun getOrders(userId: String): Flow<ResultState<List<Order>>>
    fun getOrderById(orderId: String): Flow<ResultState<Order>>
}
