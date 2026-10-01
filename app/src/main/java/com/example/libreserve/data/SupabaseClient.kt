package com.example.libreserve.data

import com.example.libreserve.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI
import java.net.URLEncoder

/** HTTPS transport for Supabase Auth and the Data API. */
object SupabaseClient {
    val isConfigured: Boolean
        get() = BuildConfig.SUPABASE_URL.isNotBlank() &&
            BuildConfig.SUPABASE_PUBLISHABLE_KEY.isNotBlank()

    private val publicTables = setOf(
        "libraries", "books", "book_copies", "reading_areas", "seats", "meeting_rooms",
        "profiles", "reservations", "notifications"
    )

    /** Pass a Supabase Auth access token to read the signed-in user's private rows. */
    suspend fun select(
        table: String,
        query: Map<String, String> = mapOf("select" to "*", "limit" to "100"),
        accessToken: String? = null
    ): JSONArray {
        require(table in publicTables) { "Unknown ReserveX table." }
        return JSONArray(request("rest/v1/$table", "GET", query, accessToken = accessToken))
    }

    suspend fun auth(
        endpoint: String,
        method: String = "POST",
        body: JSONObject? = null,
        query: Map<String, String> = emptyMap(),
        accessToken: String? = null
    ): JSONObject {
        require(endpoint in setOf("signup", "token", "user", "logout"))
        val result = request("auth/v1/$endpoint", method, query, body, accessToken)
        return if (result.isBlank()) JSONObject() else JSONObject(result)
    }

    private suspend fun request(
        path: String,
        method: String,
        query: Map<String, String>,
        body: JSONObject? = null,
        accessToken: String? = null
    ): String = withContext(Dispatchers.IO) {
        check(isConfigured) { "Configure supabase.properties and rebuild the app first." }
        val base = URI(BuildConfig.SUPABASE_URL.trimEnd('/'))
        require(base.scheme == "https" && !base.host.isNullOrBlank() &&
            base.rawUserInfo == null && base.rawQuery == null && base.rawFragment == null &&
            base.path.isNullOrEmpty()) { "SUPABASE_URL must be the HTTPS project URL without a path." }
        require(BuildConfig.SUPABASE_PUBLISHABLE_KEY.startsWith("sb_publishable_")) {
            "Use a Supabase publishable key (sb_publishable_), never a secret/service_role key."
        }
        val parameters = query.entries.joinToString("&") {
            "${encode(it.key)}=${encode(it.value)}"
        }
        val connection = URI("$base/$path?$parameters").toURL()
            .openConnection() as HttpURLConnection
        try {
            connection.requestMethod = method
            connection.connectTimeout = 15_000
            connection.readTimeout = 15_000
            connection.instanceFollowRedirects = false
            connection.setRequestProperty("apikey", BuildConfig.SUPABASE_PUBLISHABLE_KEY)
            connection.setRequestProperty("Accept", "application/json")
            // Publishable keys are not JWTs. Only a user's access token belongs here.
            accessToken?.takeIf { it.isNotBlank() }?.let {
                connection.setRequestProperty("Authorization", "Bearer $it")
            }
            if (body != null) {
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                connection.outputStream.use { it.write(body.toString().toByteArray(Charsets.UTF_8)) }
            }
            val status = connection.responseCode
            if (status !in 200..299) {
                val error = connection.errorStream?.bufferedReader()?.use { it.readText() }.orEmpty()
                val code = runCatching { JSONObject(error).optString("error_code") }.getOrDefault("")
                val message = when (code) {
                    "invalid_credentials" -> "Email or password is incorrect."
                    "email_not_confirmed" -> "Confirm your email before signing in."
                    "user_already_exists", "email_exists" -> "An account already exists. Please sign in."
                    "weak_password" -> "Choose a stronger password that meets the project's password policy."
                    "over_email_send_rate_limit", "over_request_rate_limit" -> "Too many attempts. Please try again later."
                    "signup_disabled" -> "Registration is currently disabled."
                    "reauthentication_needed", "reauthentication_not_valid", "reauthentication_required" -> "Sign in again before changing your password."
                    else -> if (status >= 500) "The server could not save your data. Please try again or check the database setup."
                        else "Supabase rejected the request (HTTP $status). Check your details and project settings."
                }
                throw SupabaseException(status, code, message)
            }
            connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }

    /** A successful empty result also proves the connection and libraries policy work. */
    suspend fun checkConnection() {
        select("libraries", mapOf("select" to "library_id", "limit" to "1"))
    }

    private fun encode(value: String): String = URLEncoder.encode(value, "UTF-8")
}

class SupabaseException(val status: Int, val code: String, message: String) : IOException(message)
