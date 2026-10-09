package com.example.libreserve.admin.reservations

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.libreserve.R
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.FirebaseFirestore

class ManageReservationsActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    private lateinit var container: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_manage_reservations
        )

        container =
            findViewById(R.id.reservationContainer)

        loadReservations()
    }

    override fun onResume() {
        super.onResume()

        loadReservations()
    }

    private fun loadReservations() {

        db.collection("reservations")
            .get()
            .addOnSuccessListener { result ->

                container.removeAllViews()

                if (result.isEmpty) {

                    val text = TextView(this)

                    text.text =
                        "No reservations found."

                    text.textSize = 16f

                    container.addView(text)

                    return@addOnSuccessListener
                }

                for (document in result.documents) {

                    val resourceName =
                        document.getString(
                            "resourceName"
                        ) ?: "Unknown resource"

                    val userId =
                        document.getString(
                            "userId"
                        ) ?: "-"

                    val type =
                        document.getString(
                            "type"
                        ) ?: "-"

                    val date =
                        document.getString(
                            "date"
                        ) ?: "-"

                    val start =
                        document.getString(
                            "startTime"
                        ) ?: "-"

                    val end =
                        document.getString(
                            "endTime"
                        ) ?: "-"

                    val status =
                        document.getString(
                            "status"
                        ) ?: "PENDING"

                    createReservationCard(
                        document.id,
                        resourceName,
                        userId,
                        type,
                        date,
                        start,
                        end,
                        status
                    )
                }
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Unable to load reservations",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun createReservationCard(
        id: String,
        resourceName: String,
        userId: String,
        type: String,
        date: String,
        start: String,
        end: String,
        status: String
    ) {

        val card = MaterialCardView(this)

        val params =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

        params.bottomMargin = 20

        card.layoutParams = params
        card.radius = 20f
        card.cardElevation = 3f

        val layout = LinearLayout(this)

        layout.orientation =
            LinearLayout.VERTICAL

        layout.setPadding(
            32,
            28,
            32,
            28
        )

        val title = TextView(this)

        title.text = resourceName
        title.textSize = 18f

        val info = TextView(this)

        info.text =
            "Type: $type\n" +
                    "User: $userId\n" +
                    "Date: $date\n" +
                    "Time: $start - $end\n" +
                    "Status: $status"

        info.textSize = 14f

        val approve = Button(this)

        approve.text = "Approve"

        approve.setOnClickListener {
            updateStatus(
                id,
                "APPROVED"
            )
        }

        val complete = Button(this)

        complete.text = "Complete"

        complete.setOnClickListener {
            updateStatus(
                id,
                "COMPLETED"
            )
        }

        val cancel = Button(this)

        cancel.text = "Cancel"

        cancel.setOnClickListener {
            updateStatus(
                id,
                "CANCELLED"
            )
        }

        layout.addView(title)
        layout.addView(info)
        layout.addView(approve)
        layout.addView(complete)
        layout.addView(cancel)

        card.addView(layout)

        container.addView(card)
    }

    private fun updateStatus(
        reservationId: String,
        status: String
    ) {

        db.collection("reservations")
            .document(reservationId)
            .update(
                "status",
                status
            )
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Reservation updated",
                    Toast.LENGTH_SHORT
                ).show()

                loadReservations()
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Update failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}