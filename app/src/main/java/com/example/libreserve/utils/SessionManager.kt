package com.example.libreserve.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.libreserve.data.SessionVault
import org.json.JSONObject

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
    private val vault = SessionVault(context)

    init {
        // Remove credentials left by older, local-only signup versions.
        prefs.edit().remove("registered_password").remove("registered_email")
            .remove("registered_name").remove("registered_student_id").apply()
    }

    val authSession: JSONObject? get() = vault.read()

    fun saveAuthSession(response: JSONObject) {
        val expiresAt = if (response.has("expires_at")) response.getLong("expires_at")
            else System.currentTimeMillis() / 1000 + response.getLong("expires_in")
        vault.save(JSONObject()
            .put("access_token", response.getString("access_token"))
            .put("refresh_token", response.getString("refresh_token"))
            .put("expires_at", expiresAt))
    }
    
    var isLoggedIn: Boolean
        get() = prefs.getBoolean(Constants.KEY_IS_LOGGED_IN, false) && authSession != null
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
        get() = prefs.getString(Constants.KEY_USER_ID, "") ?: ""
        set(value) = prefs.edit().putString(Constants.KEY_USER_ID, value).apply()
        
    var studentId: String
        get() = prefs.getString(Constants.KEY_STUDENT_ID, "") ?: ""
        set(value) = prefs.edit().putString(Constants.KEY_STUDENT_ID, value).apply()
        
    fun login(name: String, email: String, id: String, studentId: String) {
        isLoggedIn = true
        userName = name
        userEmail = email
        userId = id
        this.studentId = studentId
    }
    
    fun logout() {
        vault.clear()
        val firstLaunch = isFirstLaunch
        prefs.edit().clear().apply()
        isFirstLaunch = firstLaunch // Preserve first launch state
    }
}
