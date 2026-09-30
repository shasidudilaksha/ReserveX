package com.example.libreserve.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.libreserve.model.MeetingRoom
import com.example.libreserve.model.SlotStatus
import com.example.libreserve.model.TimeSlot
import com.example.libreserve.repository.MeetingRoomRepository

class MeetingRoomViewModel : ViewModel() {
    private val repository = MeetingRoomRepository()
    
    private val _rooms = MutableLiveData<List<MeetingRoom>>()
    val rooms: LiveData<List<MeetingRoom>> = _rooms
    
    private val _timeSlots = MutableLiveData<List<TimeSlot>>()
    val timeSlots: LiveData<List<TimeSlot>> = _timeSlots
    
    private val _selectedRoom = MutableLiveData<MeetingRoom?>()
    val selectedRoom: LiveData<MeetingRoom?> = _selectedRoom
    
    private val _selectedSlot = MutableLiveData<TimeSlot?>()
    val selectedSlot: LiveData<TimeSlot?> = _selectedSlot
    
    private var currentSlots = mutableListOf<TimeSlot>()
    private var selectedSlotId: String? = null
    
    fun loadAllRooms() {
        _rooms.value = repository.getAllRooms()
    }
    
    fun loadRooms(libraryId: String) {
        _rooms.value = repository.getRoomsByLibrary(libraryId)
    }
    
    fun loadTimeSlots(roomId: String, date: String) {
        currentSlots = repository.getTimeSlots(roomId, date).toMutableList()
        _timeSlots.value = currentSlots
    }
    
    fun selectRoom(room: MeetingRoom) {
        _selectedRoom.value = room
    }
    
    fun selectSlot(slot: TimeSlot) {
        if (slot.status != SlotStatus.AVAILABLE) return
        selectedSlotId = slot.slotId
        _selectedSlot.value = slot
    }
    
    fun deselectSlot() {
        selectedSlotId = null
        _selectedSlot.value = null
    }
    
    fun getRoomById(roomId: String): MeetingRoom? = repository.getRoomById(roomId)
}
