package com.example.libreserve.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.libreserve.model.Reservation
import com.example.libreserve.model.ReservationStatus
import com.example.libreserve.repository.ReservationRepository
import kotlinx.coroutines.launch

class ReservationViewModel : ViewModel() {
    private val repository = ReservationRepository()
    
    private val _reservations = MutableLiveData<List<Reservation>>()
    val reservations: LiveData<List<Reservation>> = _reservations
    
    val upcomingReservations: LiveData<List<Reservation>> = _reservations.map { list -> 
        list.filter { it.status == ReservationStatus.UPCOMING } 
    }
    val completedReservations: LiveData<List<Reservation>> = _reservations.map { list -> 
        list.filter { it.status == ReservationStatus.COMPLETED } 
    }
    val cancelledReservations: LiveData<List<Reservation>> = _reservations.map { list -> 
        list.filter { it.status == ReservationStatus.CANCELLED } 
    }
    
    fun loadReservations(userId: String) = viewModelScope.launch {
        _reservations.value = repository.getUserReservations(userId)
    }
    
    fun cancelReservation(reservationId: String, userId: String) = viewModelScope.launch {
        val reservation = getReservationById(reservationId)
        if (reservation?.type == com.example.libreserve.model.ReservationType.BOOK) {
            repository.cancelBookReservation(reservationId)
        } else {
            repository.cancelMockReservation(reservationId)
        }
        _reservations.value = repository.getUserReservations(userId)
    }

    fun completeBookReservation(reservationId: String, userId: String, onResult: (Result<Unit>) -> Unit) =
        viewModelScope.launch {
            val result = repository.completeBookReservation(reservationId)
            if (result.isSuccess) _reservations.value = repository.getUserReservations(userId)
            onResult(result)
        }
    
    fun addReservation(reservation: Reservation, userId: String) {
        repository.addReservation(reservation)
        loadReservations(userId)
    }

    fun addBookReservation(reservation: Reservation, userId: String, onResult: (Result<Unit>) -> Unit) =
        viewModelScope.launch {
            val result = repository.addBookReservation(reservation)
            if (result.isSuccess) _reservations.value = repository.getUserReservations(userId)
            onResult(result)
        }
    
    fun modifyReservation(reservationId: String, newDate: String, newStart: String, newEnd: String, userId: String) {
        repository.modifyReservation(reservationId, newDate, newStart, newEnd)
        loadReservations(userId)
    }
    
    fun getReservationById(id: String): Reservation? = _reservations.value?.find { it.reservationId == id }
}
