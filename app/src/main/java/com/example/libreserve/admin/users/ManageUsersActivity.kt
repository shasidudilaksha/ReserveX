package com.example.libreserve.admin.users

import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.libreserve.R
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.FirebaseFirestore

class ManageUsersActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    private lateinit var container: LinearLayout
    private lateinit var userCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_manage_users
        )

        container =
            findViewById(R.id.userContainer)

        userCount =
            findViewById(R.id.tvUserCount)

        loadUsers()
    }

    private fun loadUsers() {

        db.collection("users")
            .get()
            .addOnSuccessListener { result ->

                container.removeAllViews()

                userCount.text =
                    "Total users: ${result.size()}"

                for (document in result.documents) {

                    val name =
                        document.getString(
                            "fullName"
                        )
                            ?: document.getString(
                                "name"
                            )
                            ?: "Unknown User"

                    val email =
                        document.getString(
                            "email"
                        ) ?: "-"

                    val studentId =
                        document.getString(
                            "studentId"
                        ) ?: "-"

                    val role =
                        document.getString(
                            "role"
                        ) ?: "user"

                    createUserCard(
                        name,
                        email,
                        studentId,
                        role
                    )
                }
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Unable to load users",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun createUserCard(
        name: String,
        email: String,
        studentId: String,
        role: String
    ) {

        val card =
            MaterialCardView(this)

        val params =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

        params.bottomMargin = 20

        card.layoutParams = params
        card.radius = 20f
        card.cardElevation = 3f

        val layout =
            LinearLayout(this)

        layout.orientation =
            LinearLayout.VERTICAL

        layout.setPadding(
            32,
            28,
            32,
            28
        )

        val nameText =
            TextView(this)

        nameText.text = name
        nameText.textSize = 18f

        val details =
            TextView(this)

        details.text =
            "Email: $email\n" +
                    "Student ID: $studentId\n" +
                    "Role: $role"

        details.textSize = 14f

        layout.addView(nameText)
        layout.addView(details)

        card.addView(layout)

        container.addView(card)
    }
}