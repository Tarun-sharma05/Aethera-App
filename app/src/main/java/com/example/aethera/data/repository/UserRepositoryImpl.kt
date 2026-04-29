package com.example.aethera.data.repository

import com.example.aethera.common.ResultState
import com.example.aethera.common.USERS
import com.example.aethera.domain.models.User
import com.example.aethera.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class UserRepositoryImpl(
    private val db: FirebaseFirestore
) : UserRepository {

    override fun getUser(uid: String): Flow<ResultState<User>> = callbackFlow {
        trySend(ResultState.Loading)
        val listener = db.collection(USERS).document(uid)
            .addSnapshotListener { snap, err ->
                if (err != null) { trySend(ResultState.Error(err.message ?: "Error")); return@addSnapshotListener }
                val user = snap?.toObject(User::class.java)
                if (user != null) trySend(ResultState.Success(user))
                else trySend(ResultState.Error("User not found"))
            }
        awaitClose { listener.remove() }
    }

    override fun createUser(user: User): Flow<ResultState<Unit>> = callbackFlow {
        trySend(ResultState.Loading)
        db.collection(USERS).document(user.uid).set(user)
            .addOnSuccessListener { trySend(ResultState.Success(Unit)) }
            .addOnFailureListener { trySend(ResultState.Error(it.message ?: "Error")) }
        awaitClose()
    }

    override fun updateUser(user: User): Flow<ResultState<Unit>> = callbackFlow {
        trySend(ResultState.Loading)
        db.collection(USERS).document(user.uid).set(user)
            .addOnSuccessListener { trySend(ResultState.Success(Unit)) }
            .addOnFailureListener { trySend(ResultState.Error(it.message ?: "Error")) }
        awaitClose()
    }
}
