package com.example.libreserve.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.libreserve.data.SupabaseException
import com.example.libreserve.model.User
import com.example.libreserve.repository.UserRepository
import com.example.libreserve.utils.SessionManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.io.IOException

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = UserRepository(SessionManager(application))
    private val _busy = MutableLiveData(false)
    val busy: LiveData<Boolean> = _busy
    private val _result = MutableLiveData<AuthResult?>()
    val result: LiveData<AuthResult?> = _result

    fun consumeResult() { _result.value = null }

    fun login(email: String, password: String) = perform {
        require(email.isNotBlank() && password.isNotEmpty()) { "Enter your email and password." }
        repository.login(email, password)
        AuthResult.LoggedIn
    }

    fun register(name: String, studentId: String, email: String, password: String, confirmation: String, terms: Boolean) = perform {
        require(password == confirmation) { "Passwords do not match." }
        require(terms) { "Please accept the terms before signing up." }
        AuthResult.Registered(repository.register(name, studentId, email, password))
    }

    fun loadProfile() = perform { AuthResult.Profile(repository.currentUser(), false) }

    fun saveProfile(name: String, password: String) = perform {
        AuthResult.Profile(repository.updateProfile(name, password), true)
    }

    fun logout() = perform {
        try {
            repository.logout()
            AuthResult.LoggedOut(null)
        } catch (error: CancellationException) {
            throw error
        } catch (_: Exception) {
            AuthResult.LoggedOut("Signed out on this device. The server session could not be revoked.")
        }
    }

    private fun perform(operation: suspend () -> AuthResult) {
        if (_busy.value == true) return
        _busy.value = true
        viewModelScope.launch {
            try {
                _result.value = operation()
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                val message = when (error) {
                    is SupabaseException -> error.message
                    is IOException -> "Cannot reach Supabase. Check your connection and try again."
                    is IllegalArgumentException, is IllegalStateException -> error.message
                    else -> "Could not read the account data. Check the database setup and try again."
                }
                _result.value = AuthResult.Failure(message ?: "The request failed. Please try again.")
            } finally {
                _busy.value = false
            }
        }
    }
}

sealed class AuthResult {
    object LoggedIn : AuthResult()
    data class Registered(val confirmationRequired: Boolean) : AuthResult()
    data class Profile(val user: User, val saved: Boolean) : AuthResult()
    data class LoggedOut(val warning: String?) : AuthResult()
    data class Failure(val message: String) : AuthResult()
}
