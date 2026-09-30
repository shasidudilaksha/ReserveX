package com.example.libreserve.repository
import com.example.libreserve.model.*
import com.example.libreserve.utils.MockDataProvider

class MeetingRoomRepository {
    fun getAllRooms() = MockDataProvider.getAllMeetingRooms()
    fun getRoomsByLibrary(libraryId: String) = MockDataProvider.getMeetingRooms(libraryId)
    fun getRoomById(roomId: String) = MockDataProvider.getRoomById(roomId)
    fun getTimeSlots(roomId: String, date: String) = MockDataProvider.getTimeSlots(roomId, date)
}
