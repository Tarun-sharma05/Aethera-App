package com.example.aethera.domain.models

import com.google.firebase.firestore.PropertyName

/**
 * Product — matches AetheraAdmin Firestore schema exactly.
 * Field names mirror the admin app's ProductDataModel.
 */
data class Product(
    val id            : String  = "",
    val name          : String  = "",
    val description   : String  = "",
    val categoryName  : String  = "",
    val price         : Double  = 0.0,
    val finalPrice    : Double  = 0.0,
    val stockQuantity : Int     = 0,
    val imageUrl      : String  = "",
    @get:PropertyName("isActive")
    @set:PropertyName("isActive")
    var isActive      : Boolean = true,
    val createdAt     : Long    = 0L,
    val updatedAt     : Long    = 0L,
)
