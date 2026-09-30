package com.example.libreserve.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.libreserve.model.Book
import com.example.libreserve.model.MeetingRoom
import com.example.libreserve.model.ReadingArea
import com.example.libreserve.repository.BookRepository
import com.example.libreserve.repository.MeetingRoomRepository
import com.example.libreserve.utils.MockDataProvider

class ExploreViewModel : ViewModel() {
    private val bookRepository = BookRepository()
    private val roomRepository = MeetingRoomRepository()
    
    private val _books = MutableLiveData<List<Book>>()
    val books: LiveData<List<Book>> = _books
    
    private val _areas = MutableLiveData<List<ReadingArea>>()
    val areas: LiveData<List<ReadingArea>> = _areas
    
    private val _rooms = MutableLiveData<List<MeetingRoom>>()
    val rooms: LiveData<List<MeetingRoom>> = _rooms
    
    private val _currentFilter = MutableLiveData<String>("ALL")
    val currentFilter: LiveData<String> = _currentFilter
    
    private var query = ""

    fun loadInitial() = refresh()

    fun search(query: String) {
        this.query = query.trim()
        refresh()
    }

    fun filterByType(type: String) {
        _currentFilter.value = type
        refresh()
    }

    private fun refresh() {
        val type = _currentFilter.value ?: "ALL"
        _books.value = if (type == "ALL" || type == "BOOKS") bookRepository.searchBooks(query) else emptyList()
        _areas.value = if (type == "ALL" || type == "SEATS") MockDataProvider.getReadingAreas("lib001").filter { it.name.contains(query, true) } else emptyList()
        _rooms.value = if (type == "ALL" || type == "ROOMS") roomRepository.getAllRooms().filter { it.name.contains(query, true) } else emptyList()
    }
}
