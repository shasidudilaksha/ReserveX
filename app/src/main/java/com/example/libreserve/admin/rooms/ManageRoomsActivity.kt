package com.example.libreserve.admin.rooms

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.libreserve.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore

class ManageRoomsActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    private lateinit var container: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_manage_rooms)

        findViewById<com.google.android.material.appbar.MaterialToolbar>(
            R.id.toolbarRooms
        ).setNavigationOnClickListener {
            finish()
        }

        container = findViewById(R.id.roomContainer)

        findViewById<MaterialButton>(R.id.btnAddRoom)
            .setOnClickListener {
                addRoom()
            }

        loadRooms()
    }

    private fun addRoom() {

        val name = value(R.id.etRoomName)
        val libraryId = value(R.id.etRoomLibraryId)
        val capacity = value(R.id.etRoomCapacity).toIntOrNull()
        val floor = value(R.id.etRoomFloor).toIntOrNull()
        val location = value(R.id.etRoomLocation)

        if (
            name.isBlank() ||
            libraryId.isBlank() ||
            capacity == null ||
            floor == null ||
            location.isBlank()
        ) {

            Toast.makeText(
                this,
                "Please complete all fields",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val document =
            db.collection("meetingRooms").document()

        val room = hashMapOf(
            "roomId" to document.id,
            "name" to name,
            "libraryId" to libraryId,
            "capacity" to capacity,
            "floor" to floor,
            "location" to location,
            "equipment" to emptyList<String>(),
            "isAvailable" to true
        )

        document.set(room)
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Room added successfully",
                    Toast.LENGTH_SHORT
                ).show()

                clearFields()
                loadRooms()
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Failed: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun loadRooms() {

        db.collection("meetingRooms")
            .get()
            .addOnSuccessListener { result ->

                container.removeAllViews()

                for (document in result.documents) {

                    val name =
                        document.getString("name") ?: "Unnamed Room"

                    val capacity =
                        document.getLong("capacity") ?: 0

                    val floor =
                        document.getLong("floor") ?: 0

                    val location =
                        document.getString("location") ?: ""

                    val available =
                        document.getBoolean("isAvailable") ?: true

                    createRoomCard(
                        document.id,
                        name,
                        capacity,
                        floor,
                        location,
                        available
                    )
                }
            }
    }

    private fun createRoomCard(
        id: String,
        name: String,
        capacity: Long,
        floor: Long,
        location: String,
        available: Boolean
    ) {

        val card = MaterialCardView(this)

        val cardParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

        cardParams.bottomMargin = 20

        card.layoutParams = cardParams
        card.radius = 20f
        card.cardElevation = 3f

        val layout = LinearLayout(this)

        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(32, 28, 32, 28)

        val title = TextView(this)
        title.text = name
        title.textSize = 18f

        val info = TextView(this)

        info.text =
            "Capacity: $capacity\nFloor: $floor\nLocation: $location\n" +
                    "Status: ${if (available) "Available" else "Unavailable"}"

        info.textSize = 14f

        val statusButton = Button(this)

        statusButton.text =
            if (available)
                "Set Unavailable"
            else
                "Set Available"

        statusButton.setOnClickListener {

            db.collection("meetingRooms")
                .document(id)
                .update(
                    "isAvailable",
                    !available
                )
                .addOnSuccessListener {
                    loadRooms()
                }
        }

        val deleteButton = Button(this)

        deleteButton.text = "Delete"

        deleteButton.setOnClickListener {

            db.collection("meetingRooms")
                .document(id)
                .delete()
                .addOnSuccessListener {
                    loadRooms()
                }
        }

        layout.addView(title)
        layout.addView(info)
        layout.addView(statusButton)
        layout.addView(deleteButton)

        card.addView(layout)

        container.addView(card)
    }

    private fun value(id: Int): String =
        findViewById<TextInputEditText>(id)
            .text
            ?.toString()
            ?.trim()
            .orEmpty()

    private fun clearFields() {

        findViewById<TextInputEditText>(
            R.id.etRoomName
        ).text?.clear()

        findViewById<TextInputEditText>(
            R.id.etRoomLibraryId
        ).text?.clear()

        findViewById<TextInputEditText>(
            R.id.etRoomCapacity
        ).text?.clear()

        findViewById<TextInputEditText>(
            R.id.etRoomFloor
        ).text?.clear()

        findViewById<TextInputEditText>(
            R.id.etRoomLocation
        ).text?.clear()
    }
}