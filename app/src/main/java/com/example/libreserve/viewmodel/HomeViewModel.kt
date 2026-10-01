package com.example.libreserve.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.libreserve.model.*
import com.example.libreserve.utils.*

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val _stats = MutableLiveData<HomeStats>()
    val stats: LiveData<HomeStats> = _stats
    
    private val _upcomingReservation = MutableLiveData<Reservation?>()
    val upcomingReservation: LiveData<Reservation?> = _upcomingReservation
    
    private val _greeting = MutableLiveData<String>()
    val greeting: LiveData<String> = _greeting
    
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user
    
    fun loadHomeData(userId: String) {
        viewModelScope.launch {
            _greeting.value = DateTimeUtils.getGreeting()
            _stats.value = MockDataProvider.getHomeStats()
            _upcomingReservation.value = MockDataProvider.getUpcomingReservation(userId)
            val session = SessionManager(getApplication())
            _user.value = User(session.userId, session.userName, session.studentId, session.userEmail)
        }
    }
}
