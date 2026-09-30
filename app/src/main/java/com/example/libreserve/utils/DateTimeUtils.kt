package com.example.libreserve.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateTimeUtils {
    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }
    
    fun getTodayFormatted(): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }
    
    fun getGreeting(): String {
        val calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.HOUR_OF_DAY)) {
            in 0..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            else -> "Good Evening"
        }
    }
    
    fun getDatePlusDays(days: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, days)
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
    
    fun getDurationLabel(startTime: String, endTime: String): String {
        try {
            val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val start = format.parse(startTime) ?: return ""
            val end = format.parse(endTime) ?: return ""
            
            val diffMs = end.time - start.time
            val diffHours = diffMs / (1000 * 60 * 60)
            
            return if (diffHours == 1L) "1 hour" else "$diffHours hours"
        } catch (e: Exception) {
            return ""
        }
    }
    
    fun generateReservationId(): String {
        return "RES" + System.currentTimeMillis()
    }
}
