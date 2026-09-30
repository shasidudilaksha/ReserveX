package com.example.libreserve.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.libreserve.model.Library
import com.example.libreserve.model.ReadingArea
import com.example.libreserve.model.Seat
import com.example.libreserve.model.SeatStatus
import com.example.libreserve.repository.SeatRepository

class SeatViewModel : ViewModel() {
    private val repository = SeatRepository()
    
    private val _libraries = MutableLiveData<List<Library>>()
    val libraries: LiveData<List<Library>> = _libraries
    
    private val _areas = MutableLiveData<List<ReadingArea>>()
    val areas: LiveData<List<ReadingArea>> = _areas
    
    private val _seats = MutableLiveData<List<Seat>>()
    val seats: LiveData<List<Seat>> = _seats
    
    private val _selectedSeat = MutableLiveData<Seat?>()
    val selectedSeat: LiveData<Seat?> = _selectedSeat
    
    private val _selectedLibrary = MutableLiveData<Library?>()
    val selectedLibrary: LiveData<Library?> = _selectedLibrary
    
    private val _selectedArea = MutableLiveData<ReadingArea?>()
    val selectedArea: LiveData<ReadingArea?> = _selectedArea
    
    var selectedDateStr: String? = null
    var selectedTimeSlot: String? = null
    
    private var currentSeats = mutableListOf<Seat>()
    
    fun loadLibraries() {
        _libraries.value = repository.getLibraries()
    }
    
    fun loadAreas(libraryId: String) {
        _areas.value = repository.getReadingAreas(libraryId)
    }
    
    fun loadSeats(areaId: String) {
        currentSeats = repository.getSeats(areaId).toMutableList()
        _seats.value = currentSeats.toList()
    }
    
    fun selectSeat(seat: Seat) {
        val index = currentSeats.indexOfFirst { it.seatId == seat.seatId }
        if (index < 0) return
        
        currentSeats.forEachIndexed { i, s ->
            if (s.status == SeatStatus.SELECTED) currentSeats[i] = s.copy(status = SeatStatus.AVAILABLE)
        }
        
        currentSeats[index] = currentSeats[index].copy(status = SeatStatus.SELECTED)
        _selectedSeat.value = currentSeats[index]
        _seats.value = currentSeats.toList()
    }
    
    fun deselectSeat() {
        currentSeats.forEachIndexed { i, s ->
            if (s.status == SeatStatus.SELECTED) currentSeats[i] = s.copy(status = SeatStatus.AVAILABLE)
        }
        _selectedSeat.value = null
        _seats.value = currentSeats.toList()
    }
    
    fun selectLibrary(library: Library) {
        _selectedLibrary.value = library
    }
    
    fun selectArea(area: ReadingArea) {
        _selectedArea.value = area
    }
}
