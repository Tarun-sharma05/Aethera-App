package com.example.aethera.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aethera.common.ResultState
import com.example.aethera.domain.repository.AuthRepository
import com.example.aethera.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take

/**
 * UI state for [SettingsScreen].
 *
 * @param isLoading   True while a Firestore read/write is in flight.
 * @param name        Display name loaded from Firestore (pre-fills the editable row).
 * @param email       Read-only email from FirebaseAuth (cannot be changed without re-auth).
 * @param phone       Phone number loaded from Firestore (pre-fills the editable row).
 * @param isSaved     One-shot flag → triggers "Saved!" Snackbar. Reset via [clearSavedFlag].
 * @param isLoggedOut One-shot flag → triggers navigation to Login. Reset handled by nav layer.
 * @param error       Non-null on Firestore failure; displayed as an error message.
 */
data class SettingsUiState(
    val isLoading   : Boolean = false,
    val name        : String  = "",
    val email       : String  = "",
    val phone       : String  = "",
    val isSaved     : Boolean = false,
    val isLoggedOut : Boolean = false,
    val error       : String? = null,
)

/**
 * ViewModel for [SettingsScreen].
 *
 * Responsibilities:
 * - Load the user's profile (name, phone) from Firestore on [init].
 * - Provide [updateName] / [updatePhone] to persist individual field edits.
 * - Expose [logout] which delegates to [AuthRepository] and sets [SettingsUiState.isLoggedOut].
 *
 * Follows the same StateFlow + ResultState + callbackFlow pattern used throughout Aethera.
 */
class SettingsViewModel(
    private val userRepository : UserRepository,
    private val authRepository : AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    /** Always fresh — safe to call repeatedly without caching concerns. */
    private val userId get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    init { loadUser() }

    // ── Read ──────────────────────────────────────────────────────────────────

    /**
     * Fetches the persisted user profile and pre-populates [SettingsUiState].
     * Keeps the Firestore snapshot listener alive so live updates are reflected.
     */
    private fun loadUser() {
        if (userId.isEmpty()) return

        // Email comes from FirebaseAuth directly — no Firestore round-trip needed.
        val firebaseEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
        _uiState.value = _uiState.value.copy(email = firebaseEmail)

        userRepository.getUser(userId).onEach { result ->
            when (result) {
                is ResultState.Loading -> _uiState.value = _uiState.value.copy(isLoading = true)
                is ResultState.Success -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    name      = result.data.name,
                    phone     = result.data.phone,
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
     * Persists an updated [name] to Firestore.
     * Reads the full [User] first (`.take(1)`) so no other fields are clobbered.
     */
    fun updateName(name: String) = updateField { it.copy(name = name.trim()) }

    /**
     * Persists an updated [phone] to Firestore.
     * Reads the full [User] first (`.take(1)`) so no other fields are clobbered.
     */
    fun updatePhone(phone: String) = updateField { it.copy(phone = phone.trim()) }

    /**
     * Generic field-update helper. Reads the latest [User] snapshot once,
     * applies [transform] to produce the updated copy, then calls [UserRepository.updateUser].
     */
    private fun updateField(transform: (com.example.aethera.domain.models.User) -> com.example.aethera.domain.models.User) {
        userRepository.getUser(userId).take(1).onEach { result ->
            if (result is ResultState.Success) {
                val updated = transform(result.data)
                userRepository.updateUser(updated).onEach { updateResult ->
                    when (updateResult) {
                        is ResultState.Loading -> _uiState.value = _uiState.value.copy(isLoading = true)
                        is ResultState.Success -> _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            name      = updated.name,
                            phone     = updated.phone,
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

    // ── Auth ──────────────────────────────────────────────────────────────────

    /**
     * Signs the user out via [AuthRepository] and sets [SettingsUiState.isLoggedOut] = true.
     * The NavGraph [LaunchedEffect] listens for this flag and navigates to Login.
     */
    fun logout() {
        authRepository.logout().onEach { result ->
            if (result is ResultState.Success)
                _uiState.value = _uiState.value.copy(isLoggedOut = true)
        }.launchIn(viewModelScope)
    }

    // ── Side-effect resets ────────────────────────────────────────────────────

    /** Called after the Snackbar has been shown to prevent re-firing on recomposition. */
    fun clearSavedFlag() {
        _uiState.value = _uiState.value.copy(isSaved = false)
    }
}
