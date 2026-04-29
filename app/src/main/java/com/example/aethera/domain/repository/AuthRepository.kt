package com.example.aethera.domain.repository

import com.example.aethera.common.ResultState
import com.example.aethera.domain.models.User
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun login(email: String, password: String): Flow<ResultState<FirebaseUser>>
    fun signup(name: String, email: String, password: String): Flow<ResultState<FirebaseUser>>
    fun logout(): Flow<ResultState<Unit>>
    fun getCurrentUser(): FirebaseUser?
    fun isLoggedIn(): Boolean
}
