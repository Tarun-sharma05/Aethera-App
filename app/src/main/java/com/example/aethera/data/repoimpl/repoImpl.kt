package com.example.aethera.data.repoimpl

import com.example.aethera.common.CATEGORY
import com.example.aethera.common.ResultState
import com.example.aethera.domain.models.category
import com.example.aethera.domain.repo.repo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject


class repoImpl @Inject constructor(private val firebaseFirestore: FirebaseFirestore): repo {

    override fun getAllCategory(): Flow<ResultState<List<category>>> = callbackFlow {
        trySend(ResultState.Loading)

        firebaseFirestore.collection(CATEGORY).get()
            .addOnSuccessListener {

                val categoryData = it.documents.mapNotNull {
                    it.toObject(category::class.java)
                }


                trySend(ResultState.Success(categoryData))
            }.addOnFailureListener {
                trySend(ResultState.Error(it.toString()))
            }

        awaitClose {
            close()
          }
    }

}