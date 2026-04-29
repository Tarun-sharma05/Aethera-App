package com.example.aethera.presentation.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aethera.common.ResultState
import com.example.aethera.domain.models.Product
import com.example.aethera.domain.repository.ProductRepository
import com.example.aethera.domain.repository.WishlistRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class WishlistUiState(
    val isLoading : Boolean        = false,
    val products  : List<Product>  = emptyList(),
    val error     : String?        = null,
)

class WishlistViewModel(
    private val wishlistRepository : WishlistRepository,
    private val productRepository  : ProductRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WishlistUiState())
    val uiState: StateFlow<WishlistUiState> = _uiState.asStateFlow()

    private val userId get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    init { loadWishlist() }

    private fun loadWishlist() {
        wishlistRepository.getWishlist(userId).onEach { result ->
            when (result) {
                is ResultState.Loading -> _uiState.value = _uiState.value.copy(isLoading = true)
                is ResultState.Success -> {
                    // Resolve productIds → Product objects using existing product stream
                    // For simplicity, load all products and filter by id
                    productRepository.getProducts().onEach { pResult ->
                        if (pResult is ResultState.Success) {
                            val wishlisted = pResult.data.filter { it.id in result.data }
                            _uiState.value = _uiState.value.copy(isLoading = false, products = wishlisted)
                        }
                    }.launchIn(viewModelScope)
                }
                is ResultState.Error -> _uiState.value = _uiState.value.copy(isLoading = false, error = result.error)
            }
        }.launchIn(viewModelScope)
    }

    fun remove(productId: String) {
        wishlistRepository.removeFromWishlist(userId, productId).launchIn(viewModelScope)
    }
}
