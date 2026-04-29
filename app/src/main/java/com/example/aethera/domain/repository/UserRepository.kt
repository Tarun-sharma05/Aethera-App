package com.example.aethera.domain.repository

import com.example.aethera.common.ResultState
import com.example.aethera.domain.models.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUser(uid: String): Flow<ResultState<User>>
    fun createUser(user: User): Flow<ResultState<Unit>>
    fun updateUser(user: User): Flow<ResultState<Unit>>
}
