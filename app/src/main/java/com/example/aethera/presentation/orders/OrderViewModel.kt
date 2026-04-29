package com.example.aethera.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aethera.common.ResultState
import com.example.aethera.domain.models.Order
import com.example.aethera.domain.repository.OrderRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class OrderUiState(
    val isLoading : Boolean      = false,
    val orders    : List<Order>  = emptyList(),
    val error     : String?      = null,
)

class OrderViewModel(
    private val orderRepository: OrderRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderUiState())
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()

    private val userId get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    init { loadOrders() }

    fun loadOrders() {
        orderRepository.getOrders(userId).onEach { result ->
            when (result) {
                is ResultState.Loading -> _uiState.value = _uiState.value.copy(isLoading = true)
                is ResultState.Success -> _uiState.value = _uiState.value.copy(isLoading = false, orders = result.data)
                is ResultState.Error   -> _uiState.value = _uiState.value.copy(isLoading = false, error = result.error)
            }
        }.launchIn(viewModelScope)
    }
}

// ────────────────────────────────────────────────────────────

data class OrderDetailUiState(
    val isLoading : Boolean = false,
    val order     : Order?  = null,
    val error     : String? = null,
)

class OrderDetailViewModel(
    private val orderRepository: OrderRepository,
    private val orderId        : String,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderDetailUiState())
    val uiState: StateFlow<OrderDetailUiState> = _uiState.asStateFlow()

    init {
        orderRepository.getOrderById(orderId).onEach { result ->
            when (result) {
                is ResultState.Loading -> _uiState.value = _uiState.value.copy(isLoading = true)
                is ResultState.Success -> _uiState.value = _uiState.value.copy(isLoading = false, order = result.data)
                is ResultState.Error   -> _uiState.value = _uiState.value.copy(isLoading = false, error = result.error)
            }
        }.launchIn(viewModelScope)
    }
}
