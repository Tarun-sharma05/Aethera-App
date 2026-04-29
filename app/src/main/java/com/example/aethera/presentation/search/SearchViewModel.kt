package com.example.aethera.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aethera.common.ResultState
import com.example.aethera.domain.models.Product
import com.example.aethera.domain.repository.ProductRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class SearchUiState(
    val query     : String        = "",
    val isLoading : Boolean       = false,
    val results   : List<Product> = emptyList(),
    val error     : String?       = null,
)

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val productRepository: ProductRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _allProducts = MutableStateFlow<List<Product>>(emptyList())

    init {
        productRepository.getProducts().onEach { result ->
            if (result is ResultState.Success) _allProducts.value = result.data
        }.launchIn(viewModelScope)
    }

    fun onQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
        val filtered = if (query.isBlank()) emptyList()
        else _allProducts.value.filter {
            it.name.contains(query, ignoreCase = true) ||
            it.categoryName.contains(query, ignoreCase = true) ||
            it.description.contains(query, ignoreCase = true)
        }
        _uiState.value = _uiState.value.copy(results = filtered)
    }

    fun clearQuery() { _uiState.value = SearchUiState() }
}
