package com.example.aethera.domain.models

/** Line item stored inside an Order document */
data class OrderItem(
    val productId  : String = "",
    val name       : String = "",
    val imageUrl   : String = "",
    val price      : Double = 0.0,
    val quantity   : Int    = 1,
)
