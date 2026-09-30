package com.example.libreserve.repository
import com.example.libreserve.model.Reservation
import com.example.libreserve.utils.MockDataProvider

class ReservationRepository {
    fun getUserReservations(userId: String) = MockDataProvider.getUserReservations(userId)
    fun addReservation(reservation: Reservation) = MockDataProvider.addReservation(reservation)
    fun cancelReservation(reservationId: String) = MockDataProvider.cancelReservation(reservationId)
    fun modifyReservation(reservationId: String, date: String, start: String, end: String) =
        MockDataProvider.modifyReservation(reservationId, date, start, end)
    fun getUpcoming(userId: String) = MockDataProvider.getUpcomingReservation(userId)
}
