package com.example.aethera.presentation.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aethera.common.ResultState
import com.example.aethera.domain.models.Order
import com.example.aethera.domain.models.OrderItem
import com.example.aethera.domain.repository.CartRepository
import com.example.aethera.domain.repository.OrderRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class CheckoutUiState(
    val isLoading    : Boolean = false,
    val orderPlacedId: String? = null,
    val error        : String? = null,
)

class CheckoutViewModel(
    private val orderRepository: OrderRepository,
    private val cartRepository : CartRepository,
    private val auth           : FirebaseAuth,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    private val userId get() = auth.currentUser?.uid ?: ""

    fun placeOrder() {
        viewModelScope.launch {
            _uiState.value = CheckoutUiState(isLoading = true)
            // Fetch cart items first
            val cartResult = cartRepository.getCartItems(userId).first {
                it !is ResultState.Loading
            }
            if (cartResult is ResultState.Error) {
                _uiState.value = CheckoutUiState(error = cartResult.error)
                return@launch
            }
            val items = (cartResult as ResultState.Success).data
            if (items.isEmpty()) {
                _uiState.value = CheckoutUiState(error = "Cart is empty")
                return@launch
            }
            val orderItems = items.map { ci ->
                OrderItem(productId = ci.productId, name = ci.name, imageUrl = ci.imageUrl, price = ci.price, quantity = ci.quantity)
            }
            val total = items.sumOf { it.totalPrice }
            val order = Order(userId = userId, items = orderItems, totalAmount = total)

            orderRepository.placeOrder(order).onEach { result ->
                when (result) {
                    is ResultState.Loading -> { /* already set */ }
                    is ResultState.Success -> {
                        // Clear cart after successful order
                        cartRepository.clearCart(userId).launchIn(viewModelScope)
                        _uiState.value = CheckoutUiState(orderPlacedId = result.data)
                    }
                    is ResultState.Error -> _uiState.value = CheckoutUiState(error = result.error)
                }
            }.launchIn(viewModelScope)
        }
    }
}
