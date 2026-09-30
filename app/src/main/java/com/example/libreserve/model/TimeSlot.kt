package com.example.libreserve.model
data class TimeSlot(val slotId: String, val startTime: String, val endTime: String, var status: SlotStatus = SlotStatus.AVAILABLE, val date: String)
