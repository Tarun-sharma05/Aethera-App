package com.example.aethera.domain.usecase

import com.example.aethera.domain.repo.repo
import javax.inject.Inject

class GetAllProductUseCase @Inject constructor(private val repo : repo) {

    fun getAllProductUseCase() = repo.getAllProducts()
}