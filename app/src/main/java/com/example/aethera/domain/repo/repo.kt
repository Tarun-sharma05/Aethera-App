package com.example.aethera.domain.repo

import com.example.aethera.common.ResultState
import com.example.aethera.domain.models.category
import com.example.aethera.domain.models.productDataModel
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.flow.Flow


interface repo {

    fun getAllCategory(): Flow<ResultState<List<category>>>
    fun getAllProducts():Flow<ResultState<List<productDataModel>>>
}