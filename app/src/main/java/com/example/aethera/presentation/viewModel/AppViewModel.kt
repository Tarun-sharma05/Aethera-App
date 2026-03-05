package com.example.aethera.presentation.viewModel

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aethera.common.ResultState
import com.example.aethera.domain.models.category
import com.example.aethera.domain.usecase.GetAllCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val GetAllCategory: GetAllCategoryUseCase
) : ViewModel() {

     private val _getAllCategoryState = MutableStateFlow(GetCategoryState())
    val getAllCategoryState = _getAllCategoryState.asStateFlow()

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


}



data class GetCategoryState(
    val isLoading: Boolean = false,
    val error: String = "",
    val data: List<category?> = emptyList()
)



