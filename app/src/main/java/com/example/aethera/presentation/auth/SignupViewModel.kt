package com.example.aethera.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aethera.common.ResultState
import com.example.aethera.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class SignupUiState(
    val isLoading : Boolean = false,
    val isSuccess : Boolean = false,
    val error     : String? = null,
)

class SignupViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignupUiState())
    val uiState: StateFlow<SignupUiState> = _uiState.asStateFlow()

    fun signup(name: String, email: String, password: String) {
        authRepository.signup(name, email, password).onEach { result ->
            when (result) {
                is ResultState.Loading -> _uiState.value = SignupUiState(isLoading = true)
                is ResultState.Success -> _uiState.value = SignupUiState(isSuccess = true)
                is ResultState.Error   -> _uiState.value = SignupUiState(error = result.error)
            }
        }.launchIn(viewModelScope)
    }

    fun clearError() { _uiState.value = _uiState.value.copy(error = null) }
}
