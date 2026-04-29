package com.example.aethera.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aethera.common.ResultState
import com.example.aethera.domain.models.CartItem
import com.example.aethera.domain.repository.CartRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class CartUiState(
    val isLoading : Boolean        = false,
    val items     : List<CartItem> = emptyList(),
    val error     : String?        = null,
) {
    val totalAmount: Double get() = items.sumOf { it.totalPrice }
    val itemCount  : Int    get() = items.sumOf { it.quantity }
}

class CartViewModel(
    private val cartRepository : CartRepository,
    private val auth           : FirebaseAuth,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    private val userId get() = auth.currentUser?.uid ?: ""

    init { loadCart() }

    fun loadCart() {
        if (userId.isEmpty()) return
        cartRepository.getCartItems(userId).onEach { result ->
            when (result) {
                is ResultState.Loading -> _uiState.value = _uiState.value.copy(isLoading = true)
                is ResultState.Success -> _uiState.value = _uiState.value.copy(isLoading = false, items = result.data)
                is ResultState.Error   -> _uiState.value = _uiState.value.copy(isLoading = false, error = result.error)
            }
        }.launchIn(viewModelScope)
    }

    fun remove(productId: String) {
        cartRepository.removeFromCart(userId, productId)
            .launchIn(viewModelScope)
    }

    fun increment(item: CartItem) {
        cartRepository.updateQuantity(userId, item.productId, item.quantity + 1)
            .launchIn(viewModelScope)
    }

    fun decrement(item: CartItem) {
        if (item.quantity <= 1) {
            remove(item.productId)
        } else {
            cartRepository.updateQuantity(userId, item.productId, item.quantity - 1)
                .launchIn(viewModelScope)
        }
    }

    fun clearCart() {
        cartRepository.clearCart(userId).launchIn(viewModelScope)
    }

    fun clearError() { _uiState.value = _uiState.value.copy(error = null) }
}
