package com.example.aethera.presentation.shippingaddress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aethera.common.ResultState
import com.example.aethera.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take

/**
 * UI state for the Shipping Address screen.
 *
 * @param isLoading True while a Firestore read or write is in progress.
 * @param address   The address currently persisted in Firestore (used to pre-fill the field).
 * @param isSaved   One-shot flag that triggers the "Address saved!" Snackbar. Reset via [ShippingAddressViewModel.clearSavedFlag].
 * @param error     Non-null when the last operation failed; shown as an error message.
 */
data class ShippingAddressUiState(
    val isLoading : Boolean = false,
    val address   : String  = "",
    val isSaved   : Boolean = false,
    val error     : String? = null,
)

/**
 * ViewModel for [ShippingAddressScreen].
 *
 * Follows the same StateFlow + ResultState pattern used throughout Aethera:
 * - Reads the persisted address from Firestore on [init].
 * - Persists a new address via [saveAddress] without overwriting other user fields.
 * - Uses [clearSavedFlag] as a one-shot side-effect reset so the Snackbar fires only once.
 */
class ShippingAddressViewModel(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShippingAddressUiState())
    val uiState: StateFlow<ShippingAddressUiState> = _uiState.asStateFlow()

    private val userId get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    init { loadAddress() }

    // ── Read ─────────────────────────────────────────────────────────────────

    /**
     * Fetches the persisted address from Firestore and pre-populates [ShippingAddressUiState.address].
     * Keeps the stream alive so live Firestore updates are reflected automatically.
     */
    private fun loadAddress() {
        if (userId.isEmpty()) return

        userRepository.getUser(userId).onEach { result ->
            when (result) {
                is ResultState.Loading -> _uiState.value = _uiState.value.copy(isLoading = true)
                is ResultState.Success -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    address   = result.data.address,
                )
                is ResultState.Error   -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error     = result.error,
                )
            }
        }.launchIn(viewModelScope)
    }

    // ── Write ─────────────────────────────────────────────────────────────────

    /**
     * Persists [newAddress] to Firestore.
     *
     * Reads the full [User] first (`.take(1)` → one-shot) to avoid overwriting other fields
     * (name, email, phone, etc.), then calls [UserRepository.updateUser] with the updated copy.
     */
    fun saveAddress(newAddress: String) {
        val trimmed = newAddress.trim()
        if (trimmed.isEmpty()) return

        userRepository.getUser(userId).take(1).onEach { result ->
            if (result is ResultState.Success) {
                val updated = result.data.copy(address = trimmed)
                userRepository.updateUser(updated).onEach { updateResult ->
                    when (updateResult) {
                        is ResultState.Loading -> _uiState.value = _uiState.value.copy(isLoading = true)
                        is ResultState.Success -> _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            address   = trimmed,
                            isSaved   = true,
                        )
                        is ResultState.Error   -> _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error     = updateResult.error,
                        )
                    }
                }.launchIn(viewModelScope)
            }
        }.launchIn(viewModelScope)
    }

    /**
     * Resets [ShippingAddressUiState.isSaved] after the Snackbar has been displayed.
     * Called from a [LaunchedEffect] in the Composable to prevent re-showing on recomposition.
     */
    fun clearSavedFlag() {
        _uiState.value = _uiState.value.copy(isSaved = false)
    }
}
