package com.example.libreserve.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.ViewModel
import com.example.libreserve.model.Reservation
import com.example.libreserve.model.ReservationStatus
import com.example.libreserve.repository.ReservationRepository

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
    
    fun loadReservations(userId: String) {
        _reservations.value = repository.getUserReservations(userId)
    }
    
    fun cancelReservation(reservationId: String, userId: String) {
        repository.cancelReservation(reservationId)
        loadReservations(userId)
    }
    
    fun addReservation(reservation: Reservation, userId: String) {
        repository.addReservation(reservation)
        loadReservations(userId)
    }
    
    fun modifyReservation(reservationId: String, newDate: String, newStart: String, newEnd: String, userId: String) {
        repository.modifyReservation(reservationId, newDate, newStart, newEnd)
        loadReservations(userId)
    }
    
    fun getReservationById(id: String): Reservation? = _reservations.value?.find { it.reservationId == id }
}
