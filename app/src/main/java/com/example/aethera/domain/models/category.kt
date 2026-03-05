package com.example.aethera.domain.models

data class category (
    var name: String = "",
    var data: Long = System.currentTimeMillis(),
    var imageUrl: String = ""
)