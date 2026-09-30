package com.example.libreserve.model
data class AppNotification(val notificationId: String, val title: String, val message: String, val timestamp: String, val type: ReservationType, val isRead: Boolean = false)
