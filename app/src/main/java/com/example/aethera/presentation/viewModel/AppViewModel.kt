package com.example.aethera.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aethera.common.ResultState
import com.example.aethera.domain.models.Category
import com.example.aethera.domain.models.productDataModel
import com.example.aethera.domain.usecase.GetAllCategoryUseCase
import com.example.aethera.domain.usecase.GetAllProductUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class AppViewModel constructor(
    private val GetAllCategory: GetAllCategoryUseCase,
    private val GetAllProduct: GetAllProductUseCase
) : ViewModel() {

     private val _getAllCategoryState = MutableStateFlow(GetCategoryState())
    val getAllCategoryState = _getAllCategoryState.asStateFlow()

    private val _getAllProductState = MutableStateFlow(GetProductState())
    val getAllProductState = _getAllProductState.asStateFlow()

    fun getAllCategory(){
        viewModelScope.launch {
            GetAllCategory.getAllCategoryUseCase().collectLatest{
               when(it){
                   is ResultState.Loading -> {
                       _getAllCategoryState.value = GetCategoryState(isLoading = true)
                   }
                   is ResultState.Success -> {
                       _getAllCategoryState.value = GetCategoryState(data = it.data)
                   }
                   is ResultState.Error -> {
                       _getAllCategoryState.value = GetCategoryState(error = it.error)
                   }
               }

            }
        }

    }


    fun getAllProduct(){
        viewModelScope.launch {
            GetAllProduct.getAllProductUseCase().collectLatest{
                when(it){
                    is ResultState.Loading -> {
                        _getAllProductState.value = GetProductState(isLoading = true)
                    }
                    is ResultState.Success -> {
                        _getAllProductState.value = GetProductState(data = it.data)
                    }
                    is ResultState.Error -> {
                        _getAllProductState.value = GetProductState(error = it.error)
                    }
                }

            }
        }

    }


}



data class GetCategoryState(
    val isLoading: Boolean = false,
    val error: String = "",
    val data: List<Category?> = emptyList()
)

data class GetProductState(
    val isLoading: Boolean = false,
    val error: String = "",
    val data: List<productDataModel?> = emptyList()
)


