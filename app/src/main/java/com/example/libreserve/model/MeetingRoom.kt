package com.example.libreserve.model
data class MeetingRoom(val roomId: String, val name: String, val libraryId: String, val capacity: Int, val floor: Int, val location: String, val equipment: List<String>, val isAvailable: Boolean)
