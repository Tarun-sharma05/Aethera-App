package com.example.aethera.data.repository

import com.example.aethera.common.ResultState
import com.example.aethera.common.USERS
import com.example.aethera.domain.models.User
import com.example.aethera.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val db  : FirebaseFirestore,
) : AuthRepository {

    override fun getCurrentUser(): FirebaseUser? = auth.currentUser
    override fun isLoggedIn(): Boolean = auth.currentUser != null

    override fun login(email: String, password: String): Flow<ResultState<FirebaseUser>> = callbackFlow {
        trySend(ResultState.Loading)
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                result.user?.let { trySend(ResultState.Success(it)) }
                    ?: trySend(ResultState.Error("Login failed: user is null"))
            }
            .addOnFailureListener { trySend(ResultState.Error(it.message ?: "Login failed")) }
        awaitClose()
    }

    override fun signup(name: String, email: String, password: String): Flow<ResultState<FirebaseUser>> = callbackFlow {
        trySend(ResultState.Loading)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val fbUser = result.user
                if (fbUser == null) { trySend(ResultState.Error("Signup failed")); return@addOnSuccessListener }
                // Write user profile to Firestore
                val user = User(
                    uid       = fbUser.uid,
                    name      = name,
                    email     = email,
                    createdAt = System.currentTimeMillis(),
                )
                db.collection(USERS).document(fbUser.uid).set(user)
                    .addOnSuccessListener { trySend(ResultState.Success(fbUser)) }
                    .addOnFailureListener { trySend(ResultState.Error(it.message ?: "Profile creation failed")) }
            }
            .addOnFailureListener { trySend(ResultState.Error(it.message ?: "Signup failed")) }
        awaitClose()
    }

    override fun logout(): Flow<ResultState<Unit>> = callbackFlow {
        trySend(ResultState.Loading)
        try {
            auth.signOut()
            trySend(ResultState.Success(Unit))
        } catch (e: Exception) {
            trySend(ResultState.Error(e.message ?: "Logout failed"))
        }
        awaitClose()
    }
}
