package com.example.libreserve.repository

import com.example.libreserve.data.SupabaseClient
import com.example.libreserve.data.SupabaseException
import com.example.libreserve.model.User
import com.example.libreserve.utils.SessionManager
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONObject

class UserRepository(private val session: SessionManager) {
    suspend fun register(name: String, studentId: String, email: String, password: String): Boolean {
        val response = SupabaseClient.auth("signup", body = signupPayload(name, studentId, email, password))
        // With confirmations enabled Supabase intentionally hides whether an address exists.
        return response.optString("access_token").isBlank()
    }

    suspend fun login(email: String, password: String): User {
        val result = SupabaseClient.auth("token", query = mapOf("grant_type" to "password"),
            body = JSONObject().put("email", email.trim()).put("password", password))
        val user = loadProfile(result.getJSONObject("user"), result.getString("access_token"))
        session.saveAuthSession(result)
        session.login(user.fullName, user.email, user.userId, user.studentId)
        return user
    }

    suspend fun currentUser(): User {
        val token = accessToken()
        val user = loadProfile(SupabaseClient.auth("user", "GET", accessToken = token), token)
        session.login(user.fullName, user.email, user.userId, user.studentId)
        return user
    }

    suspend fun updateProfile(name: String, password: String): User {
        require(name.trim().isNotEmpty()) { "Enter your full name." }
        require(password.isEmpty() || password.length >= 8) { "Password must contain at least 8 characters." }
        val token = accessToken()
        val body = JSONObject().put("data", JSONObject().put("full_name", name.trim()))
        if (password.isNotEmpty()) body.put("password", password)
        val authUser = SupabaseClient.auth("user", "PUT", body, accessToken = token)
        val user = loadProfile(authUser, token)
        check(user.fullName == name.trim()) { "Profile synchronization is missing. Apply the signup alignment SQL migration." }
        session.login(user.fullName, user.email, user.userId, user.studentId)
        return user
    }

    suspend fun logout() {
        try {
            SupabaseClient.auth("logout", query = mapOf("scope" to "local"), accessToken = accessToken())
        } finally {
            session.logout()
        }
    }

    private suspend fun loadProfile(authUser: JSONObject, token: String): User {
        val id = authUser.getString("id")
        val rows = SupabaseClient.select("profiles", mapOf(
            "select" to "user_id,full_name,student_id", "user_id" to "eq.$id", "limit" to "1"
        ), token)
        check(rows.length() == 1) { "Your account profile is missing. Apply the database migrations before signing in." }
        return userFromRows(authUser, rows.getJSONObject(0))
    }

    suspend fun accessToken(): String = tokenMutex.withLock {
        val stored = session.authSession ?: error("Your session has ended. Please sign in again.")
        if (stored.getLong("expires_at") > System.currentTimeMillis() / 1000 + 60) {
            return@withLock stored.getString("access_token")
        }
        try {
            val refreshed = SupabaseClient.auth("token", query = mapOf("grant_type" to "refresh_token"),
                body = JSONObject().put("refresh_token", stored.getString("refresh_token")))
            session.saveAuthSession(refreshed)
            refreshed.getString("access_token")
        } catch (error: SupabaseException) {
            if (error.status == 400 || error.status == 401) session.logout()
            throw error
        }
    }

    companion object {
        private val tokenMutex = Mutex()

        fun signupPayload(name: String, studentId: String, email: String, password: String): JSONObject {
            require(name.trim().isNotEmpty() && studentId.trim().isNotEmpty()) { "Enter your name and student ID." }
            require(email.trim().matches(Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"))) { "Enter a valid email address." }
            require(password.length >= 8) { "Password must contain at least 8 characters." }
            return JSONObject().put("email", email.trim()).put("password", password)
                .put("data", JSONObject().put("full_name", name.trim())
                    .put("student_id", studentId.trim()).put("terms_accepted", true))
        }

        fun userFromRows(authUser: JSONObject, profile: JSONObject): User {
            check(authUser.getString("id") == profile.getString("user_id")) { "Account and profile do not match." }
            return User(profile.getString("user_id"), profile.getString("full_name"),
                if (profile.isNull("student_id")) "" else profile.getString("student_id"), authUser.getString("email"))
        }
    }
}
