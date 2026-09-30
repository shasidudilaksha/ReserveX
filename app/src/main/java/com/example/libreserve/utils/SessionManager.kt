package com.example.libreserve.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
    
    var isLoggedIn: Boolean
        get() = prefs.getBoolean(Constants.KEY_IS_LOGGED_IN, false)
        set(value) = prefs.edit().putBoolean(Constants.KEY_IS_LOGGED_IN, value).apply()
        
    var isFirstLaunch: Boolean
        get() = prefs.getBoolean(Constants.KEY_IS_FIRST_LAUNCH, true)
        set(value) = prefs.edit().putBoolean(Constants.KEY_IS_FIRST_LAUNCH, value).apply()
        
    var userName: String
        get() = prefs.getString(Constants.KEY_USER_NAME, "") ?: ""
        set(value) = prefs.edit().putString(Constants.KEY_USER_NAME, value).apply()
        
    var userEmail: String
        get() = prefs.getString(Constants.KEY_USER_EMAIL, "") ?: ""
        set(value) = prefs.edit().putString(Constants.KEY_USER_EMAIL, value).apply()
        
    var userId: String
        get() = prefs.getString(Constants.KEY_USER_ID, Constants.DEFAULT_USER_ID) ?: Constants.DEFAULT_USER_ID
        set(value) = prefs.edit().putString(Constants.KEY_USER_ID, value).apply()
        
    var studentId: String
        get() = prefs.getString(Constants.KEY_STUDENT_ID, "") ?: ""
        set(value) = prefs.edit().putString(Constants.KEY_STUDENT_ID, value).apply()
        
    var registeredEmail: String
        get() = prefs.getString("registered_email", Constants.DEMO_EMAIL) ?: Constants.DEMO_EMAIL
        set(value) = prefs.edit().putString("registered_email", value).apply()

    var registeredPassword: String
        get() = prefs.getString("registered_password", Constants.DEMO_PASSWORD) ?: Constants.DEMO_PASSWORD
        set(value) = prefs.edit().putString("registered_password", value).apply()
        
    var registeredName: String
        get() = prefs.getString("registered_name", "Student Demo") ?: "Student Demo"
        set(value) = prefs.edit().putString("registered_name", value).apply()

    var registeredStudentId: String
        get() = prefs.getString("registered_student_id", "STD001") ?: "STD001"
        set(value) = prefs.edit().putString("registered_student_id", value).apply()

    fun login(name: String, email: String, id: String, studentId: String) {
        isLoggedIn = true
        userName = name
        userEmail = email
        userId = id
        this.studentId = studentId
    }
    
    fun logout() {
        val firstLaunch = isFirstLaunch
        prefs.edit().clear().apply()
        isFirstLaunch = firstLaunch // Preserve first launch state
    }
}
