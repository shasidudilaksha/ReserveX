package com.example.libreserve.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookRow(
    val id: String,
    val title: String,
    val author: String,
    val category: String,
    val isbn: String? = null,
    val description: String? = null,
    @SerialName("shelf_location") val shelfLocation: String? = null,
    @SerialName("total_copies") val totalCopies: Int
)

@Serializable
data class BookReservationRow(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("book_id") val bookId: String,
    @SerialName("pickup_date") val pickupDate: String,
    @SerialName("pickup_time") val pickupTime: String,
    val status: String,
    @SerialName("created_at") val createdAt: String? = null
)
