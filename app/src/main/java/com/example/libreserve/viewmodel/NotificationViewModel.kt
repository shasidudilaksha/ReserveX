package com.example.libreserve.viewmodel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.libreserve.model.AppNotification
import com.example.libreserve.repository.NotificationRepository

class NotificationViewModel : ViewModel() {
    private val repository = NotificationRepository()
    private val _notifications = MutableLiveData<List<AppNotification>>()
    val notifications: LiveData<List<AppNotification>> = _notifications
    
    fun loadNotifications(userId: String) {
        _notifications.value = repository.getNotifications(userId)
    }
}
