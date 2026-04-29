package com.example.aethera.presentation.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aethera.common.ResultState
import com.example.aethera.domain.models.CartItem
import com.example.aethera.domain.models.Product
import com.example.aethera.domain.repository.CartRepository
import com.example.aethera.domain.repository.ProductRepository
import com.example.aethera.domain.repository.WishlistRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class ProductDetailUiState(
    val isLoading     : Boolean  = false,
    val product       : Product? = null,
    val isWishlisted  : Boolean  = false,
    val cartMessage   : String?  = null,
    val error         : String?  = null,
)

class ProductDetailViewModel(
    private val productRepository  : ProductRepository,
    private val cartRepository     : CartRepository,
    private val wishlistRepository : WishlistRepository,
    private val productId          : String,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    private val userId get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    init {
        loadProduct()
        checkWishlist()
    }

    private fun loadProduct() {
        productRepository.getProductById(productId).onEach { result ->
            when (result) {
                is ResultState.Loading -> _uiState.value = _uiState.value.copy(isLoading = true)
                is ResultState.Success -> _uiState.value = _uiState.value.copy(isLoading = false, product = result.data)
                is ResultState.Error   -> _uiState.value = _uiState.value.copy(isLoading = false, error = result.error)
            }
        }.launchIn(viewModelScope)
    }

    private fun checkWishlist() {
        if (userId.isEmpty()) return
        wishlistRepository.isInWishlist(userId, productId).onEach { result ->
            if (result is ResultState.Success) _uiState.value = _uiState.value.copy(isWishlisted = result.data)
        }.launchIn(viewModelScope)
    }

    fun addToCart(quantity: Int = 1) {
        val product = _uiState.value.product ?: return
        val item = CartItem(
            productId = product.id,
            name      = product.name,
            imageUrl  = product.imageUrl,
            price     = product.finalPrice.takeIf { it > 0.0 } ?: product.price,
            quantity  = quantity,
        )
        cartRepository.addToCart(userId, item).onEach { result ->
            when (result) {
                is ResultState.Success -> _uiState.value = _uiState.value.copy(cartMessage = "Added to cart!")
                is ResultState.Error   -> _uiState.value = _uiState.value.copy(error = result.error)
                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    fun toggleWishlist() {
        val wishlisted = _uiState.value.isWishlisted
        val flow = if (wishlisted) wishlistRepository.removeFromWishlist(userId, productId)
                   else            wishlistRepository.addToWishlist(userId, productId)
        flow.onEach { result ->
            if (result is ResultState.Success) _uiState.value = _uiState.value.copy(isWishlisted = !wishlisted)
        }.launchIn(viewModelScope)
    }

    fun clearMessages() { _uiState.value = _uiState.value.copy(cartMessage = null, error = null) }
}
