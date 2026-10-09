package com.example.libreserve.admin

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.libreserve.R
import com.example.libreserve.admin.books.ManageBooksActivity
import com.example.libreserve.admin.reservations.ManageReservationsActivity
import com.example.libreserve.admin.rooms.ManageRoomsActivity
import com.example.libreserve.admin.seats.ManageSeatsActivity
import com.example.libreserve.admin.users.ManageUsersActivity
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdminDashboardActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var bookCount: TextView
    private lateinit var reservationCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        bookCount = findViewById(R.id.tvBookCount)
        reservationCount = findViewById(R.id.tvReservationCount)

        findViewById<MaterialCardView>(R.id.cardBooks)
            .setOnClickListener {
                startActivity(
                    Intent(this, ManageBooksActivity::class.java)
                )
            }

        findViewById<MaterialCardView>(R.id.cardSeats)
            .setOnClickListener {
                startActivity(
                    Intent(this, ManageSeatsActivity::class.java)
                )
            }

        findViewById<MaterialCardView>(R.id.cardRooms)
            .setOnClickListener {
                startActivity(
                    Intent(this, ManageRoomsActivity::class.java)
                )
            }

        findViewById<MaterialCardView>(R.id.cardReservations)
            .setOnClickListener {
                startActivity(
                    Intent(this, ManageReservationsActivity::class.java)
                )
            }

        findViewById<MaterialCardView>(R.id.cardUsers)
            .setOnClickListener {
                startActivity(
                    Intent(this, ManageUsersActivity::class.java)
                )
            }

        findViewById<android.view.View>(R.id.btnLogout)
            .setOnClickListener {

                auth.signOut()

                val intent =
                    Intent(this, AdminLoginActivity::class.java)

                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intent)
            }

        loadStatistics()
    }

    override fun onResume() {
        super.onResume()
        loadStatistics()
    }

    private fun loadStatistics() {

        db.collection("books")
            .get()
            .addOnSuccessListener {
                bookCount.text = it.size().toString()
            }

        db.collection("reservations")
            .get()
            .addOnSuccessListener {
                reservationCount.text = it.size().toString()
            }
    }
}