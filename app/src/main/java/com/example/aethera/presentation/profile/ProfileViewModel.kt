package com.example.aethera.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aethera.common.ResultState
import com.example.aethera.domain.models.User
import com.example.aethera.domain.repository.AuthRepository
import com.example.aethera.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class ProfileUiState(
    val isLoading  : Boolean = false,
    val user       : User?   = null,
    val isLoggedOut: Boolean = false,
    val error      : String? = null,
)

class ProfileViewModel(
    private val userRepository : UserRepository,
    private val authRepository : AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val userId get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    init { loadProfile() }

    private fun loadProfile() {
        if (userId.isEmpty()) return
        userRepository.getUser(userId).onEach { result ->
            when (result) {
                is ResultState.Loading -> _uiState.value = _uiState.value.copy(isLoading = true)
                is ResultState.Success -> _uiState.value = _uiState.value.copy(isLoading = false, user = result.data)
                is ResultState.Error   -> _uiState.value = _uiState.value.copy(isLoading = false, error = result.error)
            }
        }.launchIn(viewModelScope)
    }

    fun logout() {
        authRepository.logout().onEach { result ->
            if (result is ResultState.Success) _uiState.value = _uiState.value.copy(isLoggedOut = true)
        }.launchIn(viewModelScope)
    }
}
