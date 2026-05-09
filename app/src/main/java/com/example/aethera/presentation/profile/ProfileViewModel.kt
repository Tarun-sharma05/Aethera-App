package com.example.aethera.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aethera.common.ResultState
import com.example.aethera.domain.models.User
import com.example.aethera.domain.repository.AuthRepository
import com.example.aethera.domain.repository.OrderRepository
import com.example.aethera.domain.repository.UserRepository
import com.example.aethera.domain.repository.WishlistRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class ProfileUiState(
    val isLoading     : Boolean = false,
    val user          : User?   = null,
    val isLoggedOut   : Boolean = false,
    val error         : String? = null,
    /** Fix #3: Real order count from Firestore. Replaces the hardcoded "2" value. */
    val orderCount    : Int     = 0,
    /** Fix #3: Real wishlist/saves count from Firestore. Replaces the hardcoded "2" value. */
    val wishlistCount : Int     = 0,
)

class ProfileViewModel(
    private val userRepository     : UserRepository,
    private val authRepository     : AuthRepository,
    private val orderRepository    : OrderRepository,
    private val wishlistRepository : WishlistRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val userId get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    init { loadProfile() }

    private fun loadProfile() {
        if (userId.isEmpty()) return

        // Load user profile data
        userRepository.getUser(userId).onEach { result ->
            when (result) {
                is ResultState.Loading -> _uiState.value = _uiState.value.copy(isLoading = true)
                is ResultState.Success -> _uiState.value = _uiState.value.copy(isLoading = false, user = result.data)
                is ResultState.Error   -> _uiState.value = _uiState.value.copy(isLoading = false, error = result.error)
            }
        }.launchIn(viewModelScope)

        // Fix #3: Load real order count from Firestore
        orderRepository.getOrderCount(userId).onEach { result ->
            if (result is ResultState.Success)
                _uiState.value = _uiState.value.copy(orderCount = result.data)
        }.launchIn(viewModelScope)

        // Fix #3: Load real wishlist/saves count from Firestore
        wishlistRepository.getWishlistCount(userId).onEach { result ->
            if (result is ResultState.Success)
                _uiState.value = _uiState.value.copy(wishlistCount = result.data)
        }.launchIn(viewModelScope)
    }

    /**
     * Fix #5: Persists [newAddress] to the user's Firestore document.
     * Copies the current user object so all other fields are preserved.
     */
    fun updateAddress(newAddress: String) {
        val currentUser = _uiState.value.user ?: return
        val updatedUser = currentUser.copy(address = newAddress.trim())
        userRepository.updateUser(updatedUser).onEach { result ->
            when (result) {
                is ResultState.Loading -> _uiState.value = _uiState.value.copy(isLoading = true)
                is ResultState.Success -> _uiState.value = _uiState.value.copy(isLoading = false)
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
