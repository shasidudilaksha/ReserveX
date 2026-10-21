package com.example.libreserve.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.AndroidViewModel
import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.libreserve.model.Book
import com.example.libreserve.repository.BookRepository
import kotlinx.coroutines.launch

class BookViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = BookRepository()
    private val userId = com.example.libreserve.utils.SessionManager(application).userId
    private var allBooks: List<Book> = emptyList()
    
    private val _books = MutableLiveData<List<Book>>()
    val books: LiveData<List<Book>> = _books
    
    private val _selectedBook = MutableLiveData<Book?>()
    val selectedBook: LiveData<Book?> = _selectedBook
    
    private val _categories = MutableLiveData<List<String>>()
    val categories: LiveData<List<String>> = _categories
    
    fun loadBooks() = viewModelScope.launch {
        allBooks = repository.getAllBooks(userId)
        _books.value = allBooks
    }
    
    fun searchBooks(query: String) {
        val q = query.trim()
        _books.value = if (q.isBlank()) allBooks else allBooks.filter {
            it.title.contains(q, true) || it.author.contains(q, true) ||
                it.category.contains(q, true) || it.isbn.contains(q, true)
        }
    }
    
    fun filterByCategory(category: String) {
        _books.value = if (category == "All") allBooks else allBooks.filter { it.category == category }
    }
    
    fun selectBook(book: Book) {
        _selectedBook.value = book
    }
    
    fun loadBook(bookId: String) = viewModelScope.launch {
        _selectedBook.value = repository.getBookById(bookId, userId)
    }
    
    fun loadCategories() = viewModelScope.launch {
        _categories.value = repository.getCategories()
    }
}
