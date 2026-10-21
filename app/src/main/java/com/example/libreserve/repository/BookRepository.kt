package com.example.libreserve.repository
import com.example.libreserve.data.BookRow
import com.example.libreserve.data.BookReservationRow
import com.example.libreserve.data.SupabaseClientProvider
import com.example.libreserve.model.Book
import io.github.jan.supabase.postgrest.from

class BookRepository {
    private val supabase = SupabaseClientProvider.client

    suspend fun getAllBooks(userId: String? = null): List<Book> {
        val rows = supabase.from("books").select().decodeList<BookRow>()
        val activeReservations = supabase.from("reservations").select {
            filter { eq("status", "ACTIVE") }
        }.decodeList<BookReservationRow>()
        val reservedByBook = activeReservations.groupingBy { it.bookId }.eachCount()
        val reservedBookIds = activeReservations
            .filter { userId != null && it.userId == userId }
            .map { it.bookId }
            .toSet()
        return rows.map { row ->
            row.toModel(reservedByBook[row.id] ?: 0, row.id in reservedBookIds)
        }
    }

    suspend fun getBookById(bookId: String, userId: String? = null): Book? =
        getAllBooks(userId).firstOrNull { it.bookId == bookId }

    suspend fun getCategories(): List<String> =
        getAllBooks().map { it.category }.distinct().sorted().let { listOf("All") + it }

    private fun BookRow.toModel(activeReservations: Int, isReservedByUser: Boolean): Book = Book(
        bookId = id,
        title = title,
        author = author,
        category = category,
        isbn = isbn.orEmpty(),
        description = description.orEmpty(),
        shelfLocation = shelfLocation.orEmpty(),
        totalCopies = totalCopies,
        availableCopies = (totalCopies - activeReservations).coerceAtLeast(0),
        isReservedByUser = isReservedByUser
    )
}
