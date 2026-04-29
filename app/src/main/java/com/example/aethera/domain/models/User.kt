package com.example.aethera.domain.models

/** User profile stored in USERS/{uid} */
data class User(
    val uid       : String = "",
    val name      : String = "",
    val email     : String = "",
    val phone     : String = "",
    val address   : String = "",
    val createdAt : Long   = 0L,
)
