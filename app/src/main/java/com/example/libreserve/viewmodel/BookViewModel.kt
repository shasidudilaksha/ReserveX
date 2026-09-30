package com.example.libreserve.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.libreserve.model.Book
import com.example.libreserve.repository.BookRepository

class BookViewModel : ViewModel() {
    private val repository = BookRepository()
    
    private val _books = MutableLiveData<List<Book>>()
    val books: LiveData<List<Book>> = _books
    
    private val _selectedBook = MutableLiveData<Book?>()
    val selectedBook: LiveData<Book?> = _selectedBook
    
    private val _categories = MutableLiveData<List<String>>()
    val categories: LiveData<List<String>> = _categories
    
    fun loadBooks() {
        _books.value = repository.getAllBooks()
    }
    
    fun searchBooks(query: String) {
        _books.value = repository.searchBooks(query)
    }
    
    fun filterByCategory(category: String) {
        _books.value = repository.filterByCategory(category)
    }
    
    fun selectBook(book: Book) {
        _selectedBook.value = book
    }
    
    fun getBook(bookId: String): Book? = repository.getBookById(bookId)
    
    fun loadCategories() {
        _categories.value = repository.getCategories()
    }
}
