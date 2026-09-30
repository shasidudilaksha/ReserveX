package com.example.libreserve.repository
import com.example.libreserve.model.AppNotification
import com.example.libreserve.utils.MockDataProvider

class NotificationRepository {
    fun getNotifications(userId: String): List<AppNotification> = MockDataProvider.getNotifications(userId)
}
