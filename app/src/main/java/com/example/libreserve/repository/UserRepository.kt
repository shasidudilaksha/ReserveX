package com.example.libreserve.repository
import com.example.libreserve.model.User
import com.example.libreserve.utils.Constants
import com.example.libreserve.utils.MockDataProvider

class UserRepository {
    fun getUser(): User = MockDataProvider.getUser()
    fun validateLogin(email: String, password: String): Boolean =
        (email == Constants.DEMO_EMAIL && password == Constants.DEMO_PASSWORD) || (email.contains("@") && password.length >= 6)
    fun register(name: String, studentId: String, email: String, password: String): Boolean =
        email.isNotBlank() && name.isNotBlank() && studentId.isNotBlank() && password.length >= 6
}
