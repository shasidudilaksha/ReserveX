package com.example.libreserve.admin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.libreserve.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdminLoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loginButton: MaterialButton
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        emailInput = findViewById(R.id.etAdminEmail)
        passwordInput = findViewById(R.id.etAdminPassword)
        loginButton = findViewById(R.id.btnAdminLogin)
        progressBar = findViewById(R.id.progressLogin)

        loginButton.setOnClickListener {
            loginAdmin()
        }
    }

    private fun loginAdmin() {

        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString()

        if (email.isEmpty()) {
            emailInput.error = "Enter admin email"
            return
        }

        if (password.isEmpty()) {
            passwordInput.error = "Enter password"
            return
        }

        setLoading(true)

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                val uid = auth.currentUser?.uid

                if (uid == null) {
                    setLoading(false)
                    return@addOnSuccessListener
                }

                db.collection("users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener { document ->

                        val role = document.getString("role")

                        if (document.exists() && role == "admin") {

                            startActivity(
                                Intent(
                                    this,
                                    AdminDashboardActivity::class.java
                                )
                            )

                            finish()

                        } else {

                            auth.signOut()

                            Toast.makeText(
                                this,
                                "You are not authorised as an administrator.",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        setLoading(false)
                    }
                    .addOnFailureListener {

                        auth.signOut()
                        setLoading(false)

                        Toast.makeText(
                            this,
                            "Unable to verify administrator role.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            .addOnFailureListener {

                setLoading(false)

                Toast.makeText(
                    this,
                    it.message ?: "Login failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun setLoading(loading: Boolean) {

        progressBar.visibility =
            if (loading) View.VISIBLE else View.GONE

        loginButton.isEnabled = !loading
        emailInput.isEnabled = !loading
        passwordInput.isEnabled = !loading
    }
}