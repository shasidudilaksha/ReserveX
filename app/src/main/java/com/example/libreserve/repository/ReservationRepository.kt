package com.example.libreserve.repository
import com.example.libreserve.data.BookReservationRow
import com.example.libreserve.data.SupabaseClientProvider
import com.example.libreserve.model.Reservation
import com.example.libreserve.model.ReservationStatus
import com.example.libreserve.model.ReservationType
import com.example.libreserve.utils.MockDataProvider
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.Serializable

class ReservationRepository {
    private val supabase = SupabaseClientProvider.client
    private val bookRepository = BookRepository()

    suspend fun getUserReservations(userId: String): List<Reservation> {
        val mockReservations = MockDataProvider.getUserReservations(userId)
            .filter { it.type != ReservationType.BOOK }
        val books = bookRepository.getAllBooks().associateBy { it.bookId }
        val remoteReservations = supabase.from("reservations").select {
            filter { eq("user_id", userId) }
        }.decodeList<BookReservationRow>().mapNotNull { row ->
            val book = books[row.bookId] ?: return@mapNotNull null
            Reservation(
                reservationId = row.id,
                userId = row.userId,
                type = ReservationType.BOOK,
                resourceId = book.bookId,
                resourceName = book.title,
                libraryId = "lib001",
                libraryName = "SLIIT Malabe Library",
                date = row.pickupDate,
                startTime = row.pickupTime,
                endTime = row.pickupTime,
                status = when (row.status) {
                    "COMPLETED" -> ReservationStatus.COMPLETED
                    "CANCELLED" -> ReservationStatus.CANCELLED
                    else -> ReservationStatus.UPCOMING
                },
                location = "Shelf ${book.shelfLocation}",
                createdAt = row.createdAt.orEmpty()
            )
        }
        return mockReservations + remoteReservations
    }

    suspend fun addBookReservation(reservation: Reservation): Result<Unit> = runCatching {
        val existingReservation = supabase.from("reservations").select {
            filter {
                eq("user_id", reservation.userId)
                eq("book_id", reservation.resourceId)
                eq("status", "ACTIVE")
            }
        }.decodeList<BookReservationRow>()
        if (existingReservation.isNotEmpty()) {
            error("You already reserved this book")
        }

        val book = bookRepository.getBookById(reservation.resourceId, reservation.userId)
            ?: error("Book not found")
        if (!book.isAvailable) error("This book is currently unavailable")
        supabase.from("reservations").insert(
            BookReservationInsert(
                id = reservation.reservationId,
                userId = reservation.userId,
                bookId = reservation.resourceId,
                pickupDate = reservation.date,
                pickupTime = reservation.startTime,
                status = "ACTIVE"
            )
        )
    }

    suspend fun cancelBookReservation(reservationId: String): Result<Unit> = runCatching {
        supabase.from("reservations").update(BookReservationUpdate("CANCELLED")) {
            filter { eq("id", reservationId) }
        }
    }

    suspend fun completeBookReservation(reservationId: String): Result<Unit> = runCatching {
        supabase.from("reservations").update(BookReservationUpdate("COMPLETED")) {
            filter { eq("id", reservationId) }
        }
    }

    fun addReservation(reservation: Reservation) = MockDataProvider.addReservation(reservation)
    fun cancelMockReservation(reservationId: String) = MockDataProvider.cancelReservation(reservationId)
    fun modifyReservation(reservationId: String, date: String, start: String, end: String) =
        MockDataProvider.modifyReservation(reservationId, date, start, end)
}

@Serializable
private data class BookReservationInsert(
    val id: String,
    @kotlinx.serialization.SerialName("user_id") val userId: String,
    @kotlinx.serialization.SerialName("book_id") val bookId: String,
    @kotlinx.serialization.SerialName("pickup_date") val pickupDate: String,
    @kotlinx.serialization.SerialName("pickup_time") val pickupTime: String,
    val status: String
)

@Serializable
private data class BookReservationUpdate(val status: String)
