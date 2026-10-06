package com.example.libreserve.viewmodel
import androidx.lifecycle.ViewModel
import com.example.libreserve.repository.UserRepository

class AuthViewModel : ViewModel() {
    private val repository = UserRepository()
    fun login(email: String, password: String): Boolean = repository.validateLogin(email.trim(), password)
    fun register(name: String, studentId: String, email: String, password: String): Boolean =
        repository.register(name, studentId, email, password)
}
