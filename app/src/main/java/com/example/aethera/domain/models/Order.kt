package com.example.aethera.domain.models

/**
 * Order — matches the ORDERS Firestore collection schema.
 * orderId is stored as a document field as well as being the document ID.
 */
data class Order(
    val orderId       : String      = "",
    val userId        : String      = "",
    val items         : List<OrderItem> = emptyList(),
    val totalAmount   : Double      = 0.0,
    val status        : String      = "Pending",     // Pending | Processing | Shipped | Delivered | Cancelled
    val paymentStatus : String      = "Unpaid",      // Unpaid | Paid
    val createdAt     : Long        = 0L,
)
