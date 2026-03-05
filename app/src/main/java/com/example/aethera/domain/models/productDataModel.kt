package com.example.aethera.domain.models

data class productDataModel(
    val name : String = "",
    val decription: String = "",
    val price : String = "",
    val finalPrice : String = "",
    val category : String = "",
    val image : String = "",
    val date : Long = System.currentTimeMillis(),
    val availableUnits : Int = 0,
    val isAvailable : Boolean = true,
    val productId : String = ""
)