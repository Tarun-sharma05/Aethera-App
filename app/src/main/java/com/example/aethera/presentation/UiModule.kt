package com.example.aethera.presentation

import com.example.aethera.data.repoimpl.repoImpl
import com.example.aethera.domain.repo.repo
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UiModule {

    @Provides
    fun provideRepo(
        firestore: FirebaseFirestore
    ): repo {
        return repoImpl(
            firestore
        )
    }
}