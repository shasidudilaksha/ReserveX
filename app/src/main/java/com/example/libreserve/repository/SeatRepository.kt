package com.example.libreserve.repository
import com.example.libreserve.model.*
import com.example.libreserve.utils.MockDataProvider

class SeatRepository {
    fun getLibraries() = MockDataProvider.getLibraries()
    fun getReadingAreas(libraryId: String) = MockDataProvider.getReadingAreas(libraryId)
    fun getSeats(areaId: String) = MockDataProvider.getSeats(areaId)
}
