package com.example.libreserve.repository
import com.example.libreserve.model.Book
import com.example.libreserve.utils.MockDataProvider

class BookRepository {
    fun getAllBooks(): List<Book> = MockDataProvider.getBooks()
    fun searchBooks(query: String): List<Book> = MockDataProvider.searchBooks(query)
    fun getBookById(bookId: String): Book? = MockDataProvider.getBookById(bookId)
    fun getCategories(): List<String> = MockDataProvider.getBookCategories()
    fun filterByCategory(category: String): List<Book> =
        if (category == "All") MockDataProvider.getBooks()
        else MockDataProvider.getBooks().filter { it.category == category }
    fun filterByAvailability(): List<Book> = MockDataProvider.getBooks().filter { it.isAvailable }
}
