//package com.example.libreserve.admin.books
//
//class AdminBook {
//}

package com.example.libreserve.admin.books

data class AdminBook(
    var bookId: String = "",
    var title: String = "",
    var author: String = "",
    var category: String = "",
    var isbn: String = "",
    var description: String = "",
    var shelfLocation: String = "",
    var totalCopies: Int = 0,
    var availableCopies: Int = 0
)