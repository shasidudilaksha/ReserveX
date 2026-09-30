package com.example.libreserve.utils
import com.example.libreserve.model.*

object MockDataProvider {
    private val _reservations = mutableListOf<Reservation>()
    private var reservationsInitialized = false

    fun getUser() = User("user001", "Nimal Perera", "IT21000001", "student@university.edu")

    fun getLibraries() = listOf(
        Library("lib001", "SLIIT Malabe Library", "Malabe Campus", "New Kandy Rd, Malabe", 40, "08:00 AM", "09:00 PM"),
        Library("lib002", "SLIIT Kandy Library", "Kandy Campus", "Kandy Road, Kandy", 30, "08:00 AM", "08:00 PM"),
        Library("lib003", "SLIIT Metro Campus Library", "Metro Campus", "Metro Campus, Colombo", 20, "09:00 AM", "07:00 PM")
    )

    fun getBooks() = listOf(
        Book("book001", "Clean Code", "Robert C. Martin", "Software Engineering", "978-0132350884", "A handbook of agile software craftsmanship. Even bad code can function. But if code isn't clean, it can bring a development organization to its knees. The cleanup doesn't come for free.", "SE-102", 3, 2),
        Book("book002", "Introduction to Algorithms", "Thomas H. Cormen", "Computer Science", "978-0262033848", "The book covers a broad range of algorithms in depth, yet makes their design and analysis accessible to all levels of readers.", "CS-201", 2, 1),
        Book("book003", "Database System Concepts", "Abraham Silberschatz", "Database Systems", "978-0078022159", "Widely used as a textbook for database courses, providing in-depth coverage of database design, languages, and systems.", "DB-105", 4, 4),
        Book("book004", "Computer Networks", "Andrew S. Tanenbaum", "Networking", "978-0132126953", "A thorough examination of the entire field of computer networking, from the physical layer up through the application layer.", "NW-301", 2, 0),
        Book("book005", "Design Patterns", "Erich Gamma", "Software Engineering", "978-0201633610", "Capturing the expertise of experienced object-oriented software developers, helping you design reusable and flexible software.", "SE-110", 3, 2),
        Book("book006", "The Pragmatic Programmer", "David Thomas", "Software Engineering", "978-0135957059", "Examines the core process of writing robust, adaptable, high-quality code. Covering topics from personal responsibility and career development to architectural techniques.", "SE-115", 2, 2),
        Book("book007", "Artificial Intelligence: A Modern Approach", "Stuart Russell", "Artificial Intelligence", "978-0134610993", "The most comprehensive, up-to-date introduction to the theory and practice of artificial intelligence.", "AI-401", 3, 1),
        Book("book008", "Operating System Concepts", "Abraham Silberschatz", "Operating Systems", "978-1118063330", "The foundational concepts of operating systems, covering processes, threads, CPU scheduling, memory management, storage and I/O.", "OS-205", 4, 3),
        Book("book009", "Discrete Mathematics and Its Applications", "Kenneth H. Rosen", "Mathematics", "978-0073383095", "A precise, relevant, comprehensive tool for mastering the basics of discrete mathematics.", "MA-102", 5, 5),
        Book("book010", "Software Engineering", "Ian Sommerville", "Software Engineering", "978-0133943030", "A comprehensive introduction to the software engineering process, covering both process and technical aspects.", "SE-120", 3, 0),
        Book("book011", "Data Structures and Algorithms in Java", "Robert Lafore", "Computer Science", "978-0672324536", "Learn data structures and algorithms using Java. Covers arrays, linked lists, stacks, queues, trees, graphs, and sorting algorithms.", "CS-210", 2, 1),
        Book("book012", "Computer Organization and Architecture", "William Stallings", "Computer Architecture", "978-0134101613", "A systematic introduction to computer organization and architecture, covering fundamental concepts through contemporary design.", "CA-301", 3, 2)
    )

    fun searchBooks(query: String): List<Book> {
        val q = query.lowercase()
        return if (q.isBlank()) getBooks()
        else getBooks().filter {
            it.title.lowercase().contains(q) || it.author.lowercase().contains(q) || it.category.lowercase().contains(q)
        }
    }

    fun getBookById(bookId: String): Book? = getBooks().find { it.bookId == bookId }

    fun getBookCategories(): List<String> = listOf("All") + getBooks().map { it.category }.distinct().sorted()

    fun getReadingAreas(libraryId: String) = listOf(
        ReadingArea("${libraryId}_area_a", "Reading Area A", libraryId, 20, 14, 1),
        ReadingArea("${libraryId}_area_b", "Reading Area B", libraryId, 20, 8, 2)
    )

    fun getSeats(areaId: String): List<Seat> {
        val seats = mutableListOf<Seat>()
        val reservedIndexes = setOf(2, 5, 8, 11, 14, 17)
        val unavailableIndexes = setOf(19)
        for (row in 0 until 5) {
            for (col in 0 until 4) {
                val index = row * 4 + col
                val seatLetter = ('A' + row).toString()
                val seatNum = col + 1
                val seatNumber = "$seatLetter-$seatNum"
                val status = when (index) {
                    in reservedIndexes -> SeatStatus.RESERVED
                    in unavailableIndexes -> SeatStatus.UNAVAILABLE
                    else -> SeatStatus.AVAILABLE
                }
                seats.add(Seat(
                    seatId = "${areaId}_seat_${index + 1}",
                    seatNumber = seatNumber,
                    areaId = areaId,
                    libraryId = areaId.substringBefore("_area"),
                    status = status,
                    row = row,
                    col = col
                ))
            }
        }
        return seats
    }

    fun getAllMeetingRooms() = listOf(
        MeetingRoom("room001", "Meeting Room A", "lib001", 8, 2, "SLIIT Malabe – Floor 2", listOf("Projector", "Whiteboard", "Wi-Fi"), true),
        MeetingRoom("room002", "Meeting Room B", "lib001", 6, 2, "SLIIT Malabe – Floor 2", listOf("Whiteboard", "Wi-Fi"), true),
        MeetingRoom("room003", "Conference Room", "lib001", 12, 3, "SLIIT Malabe – Floor 3", listOf("Projector", "Video Conference", "Whiteboard", "Wi-Fi"), true),
        MeetingRoom("room004", "Study Pod A", "lib002", 4, 1, "SLIIT Kandy – Floor 1", listOf("Whiteboard", "Wi-Fi"), true),
        MeetingRoom("room005", "Group Study Room", "lib003", 8, 1, "SLIIT Metro – Floor 1", listOf("Projector", "Wi-Fi"), true)
    )

    fun getMeetingRooms(libraryId: String) = getAllMeetingRooms().filter { it.libraryId == libraryId }
    fun getRoomById(roomId: String) = getAllMeetingRooms().find { it.roomId == roomId }

    fun getTimeSlots(roomId: String, date: String): List<TimeSlot> {
        val reservedIndexes = when (roomId) {
            "room001" -> setOf(2, 4)
            "room002" -> setOf(1, 5)
            "room003" -> setOf(0, 3)
            else -> setOf(3)
        }
        val slotTimes = listOf(
            Pair("09:00 AM", "10:00 AM"),
            Pair("10:00 AM", "11:00 AM"),
            Pair("11:00 AM", "12:00 PM"),
            Pair("12:00 PM", "01:00 PM"),
            Pair("01:00 PM", "02:00 PM"),
            Pair("02:00 PM", "03:00 PM"),
            Pair("03:00 PM", "05:00 PM"),
            Pair("05:00 PM", "07:00 PM")
        )
        return slotTimes.mapIndexed { index, (start, end) ->
            TimeSlot(
                slotId = "${roomId}_slot_$index",
                startTime = start,
                endTime = end,
                status = if (index in reservedIndexes) SlotStatus.RESERVED else SlotStatus.AVAILABLE,
                date = date
            )
        }
    }

    fun getUserReservations(userId: String): List<Reservation> {
        ensureInitialized()
        return _reservations.filter { it.userId == userId }
    }

    private fun ensureInitialized() {
        if (!reservationsInitialized) {
            reservationsInitialized = true
            _reservations.addAll(listOf(
                Reservation("res001", "user001", ReservationType.SEAT, "lib001_area_a_seat_1", "Seat A-1", "lib001", "SLIIT Malabe Library", "14 September 2026", "10:00 AM", "12:00 PM", ReservationStatus.UPCOMING, "Reading Area A"),
                Reservation("res002", "user001", ReservationType.MEETING_ROOM, "room001", "Meeting Room A", "lib001", "SLIIT Malabe Library", "15 September 2026", "02:00 PM", "04:00 PM", ReservationStatus.UPCOMING, "Floor 2"),
                Reservation("res003", "user001", ReservationType.BOOK, "book001", "Clean Code", "lib001", "SLIIT Malabe Library", "20 September 2026", "10:00 AM", "10:00 AM", ReservationStatus.UPCOMING, "Shelf SE-102"),
                Reservation("res004", "user001", ReservationType.SEAT, "lib001_area_b_seat_3", "Seat B-3", "lib001", "SLIIT Malabe Library", "10 September 2026", "09:00 AM", "11:00 AM", ReservationStatus.COMPLETED, "Reading Area B"),
                Reservation("res005", "user001", ReservationType.MEETING_ROOM, "room002", "Meeting Room B", "lib001", "SLIIT Malabe Library", "08 September 2026", "01:00 PM", "02:00 PM", ReservationStatus.COMPLETED, "Floor 2"),
                Reservation("res006", "user001", ReservationType.BOOK, "book003", "Database System Concepts", "lib001", "SLIIT Malabe Library", "05 September 2026", "10:00 AM", "10:00 AM", ReservationStatus.COMPLETED, "Shelf DB-105"),
                Reservation("res007", "user001", ReservationType.SEAT, "lib002_area_a_seat_7", "Seat B-3", "lib002", "SLIIT Kandy Library", "01 September 2026", "02:00 PM", "04:00 PM", ReservationStatus.CANCELLED, "Reading Area A"),
                Reservation("res008", "user001", ReservationType.MEETING_ROOM, "room003", "Conference Room", "lib001", "SLIIT Malabe Library", "03 September 2026", "10:00 AM", "12:00 PM", ReservationStatus.CANCELLED, "Floor 3")
            ))
        }
    }

    fun addReservation(reservation: Reservation) {
        ensureInitialized()
        _reservations.add(0, reservation)
    }

    fun cancelReservation(reservationId: String) {
        ensureInitialized()
        val index = _reservations.indexOfFirst { it.reservationId == reservationId }
        if (index >= 0) _reservations[index] = _reservations[index].copy(status = ReservationStatus.CANCELLED)
    }

    fun modifyReservation(reservationId: String, newDate: String, newStartTime: String, newEndTime: String) {
        ensureInitialized()
        val index = _reservations.indexOfFirst { it.reservationId == reservationId }
        if (index >= 0) _reservations[index] = _reservations[index].copy(date = newDate, startTime = newStartTime, endTime = newEndTime)
    }

    fun getHomeStats() = HomeStats(
        booksAvailable = getBooks().sumOf { it.availableCopies },
        seatsAvailable = 18,
        roomsAvailable = getAllMeetingRooms().count { it.isAvailable }
    )

    fun getUpcomingReservation(userId: String): Reservation? {
        ensureInitialized()
        return _reservations.firstOrNull { it.userId == userId && it.status == ReservationStatus.UPCOMING }
    }

    fun getNotifications(userId: String) = listOf(
        AppNotification("notif001", "Reservation Reminder", "Your reading seat reservation starts in 30 minutes. Seat A-1, Reading Area A.", "Today, 09:30 AM", ReservationType.SEAT, false),
        AppNotification("notif002", "Book Ready for Collection", "Your book reservation for 'Clean Code' is ready for collection at Shelf SE-102.", "Today, 08:00 AM", ReservationType.BOOK, false),
        AppNotification("notif003", "Upcoming Meeting Room", "Meeting Room A is reserved for tomorrow at 02:00 PM. Don't forget!", "Yesterday, 06:00 PM", ReservationType.MEETING_ROOM, true),
        AppNotification("notif004", "Reservation Confirmed", "Your seat reservation for Reading Area A, Seat A-1 has been confirmed for 14 September.", "2 days ago", ReservationType.SEAT, true),
        AppNotification("notif005", "Reservation Cancelled", "Your reading seat reservation for 01 September has been cancelled as requested.", "3 days ago", ReservationType.SEAT, true)
    )
}
