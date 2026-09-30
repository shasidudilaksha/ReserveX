package com.example.libreserve.model
data class Seat(val seatId: String, val seatNumber: String, val areaId: String, val libraryId: String, var status: SeatStatus = SeatStatus.AVAILABLE, val row: Int, val col: Int)
