package com.example.libreserve.model
data class Book(val bookId: String, val title: String, val author: String, val category: String, val isbn: String, val description: String, val shelfLocation: String, val totalCopies: Int, val availableCopies: Int) {
    val isAvailable: Boolean get() = availableCopies > 0
}
