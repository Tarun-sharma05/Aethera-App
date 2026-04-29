package com.example.aethera.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aethera.common.ResultState
import com.example.aethera.domain.models.Category
import com.example.aethera.domain.models.Product
import com.example.aethera.domain.repository.ProductRepository
import com.example.aethera.domain.repository.WishlistRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class HomeUiState(
    val isLoading         : Boolean       = false,
    val products          : List<Product> = emptyList(),
    val categories        : List<Category> = emptyList(),
    val selectedCategory  : String        = "All",
    val error             : String?       = null,
)

class HomeViewModel(
    private val productRepository  : ProductRepository,
    private val wishlistRepository : WishlistRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
        loadProducts()
    }

    private fun loadCategories() {
        productRepository.getCategories().onEach { result ->
            when (result) {
                is ResultState.Loading -> { /* categories load silently */ }
                is ResultState.Success -> _uiState.value = _uiState.value.copy(categories = result.data)
                is ResultState.Error   -> _uiState.value = _uiState.value.copy(error = result.error)
            }
        }.launchIn(viewModelScope)
    }

    fun loadProducts() {
        productRepository.getProducts().onEach { result ->
            when (result) {
                is ResultState.Loading -> _uiState.value = _uiState.value.copy(isLoading = true)
                is ResultState.Success -> _uiState.value = _uiState.value.copy(isLoading = false, products = result.data)
                is ResultState.Error   -> _uiState.value = _uiState.value.copy(isLoading = false, error = result.error)
            }
        }.launchIn(viewModelScope)
    }

    fun selectCategory(category: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        if (category == "All") {
            loadProducts()
        } else {
            productRepository.getProductsByCategory(category).onEach { result ->
                when (result) {
                    is ResultState.Loading -> _uiState.value = _uiState.value.copy(isLoading = true)
                    is ResultState.Success -> _uiState.value = _uiState.value.copy(isLoading = false, products = result.data)
                    is ResultState.Error   -> _uiState.value = _uiState.value.copy(isLoading = false, error = result.error)
                }
            }.launchIn(viewModelScope)
        }
    }

    fun clearError() { _uiState.value = _uiState.value.copy(error = null) }
}
