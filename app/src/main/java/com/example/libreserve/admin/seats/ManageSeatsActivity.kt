//package com.example.libreserve.admin.seats
//
//import android.os.Bundle
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.example.libreserve.R
//import com.google.firebase.firestore.FirebaseFirestore
//
//class ManageSeatsActivity : AppCompatActivity() {
//
//    private val db = FirebaseFirestore.getInstance()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        setContentView(R.layout.activity_manage_seats)
//
//        addSeat()
//    }
//
//    private fun addSeat() {
//
//        val document =
//            db.collection("seats").document()
//
//        val seat = hashMapOf(
//            "seatId" to document.id,
//            "seatNumber" to "A01",
//            "areaId" to "READING_A",
//            "libraryId" to "MAIN",
//            "status" to "AVAILABLE",
//            "row" to 1,
//            "col" to 1
//        )
//
//        document.set(seat)
//            .addOnSuccessListener {
//
//                Toast.makeText(
//                    this,
//                    "Seat added successfully",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//            .addOnFailureListener {
//
//                Toast.makeText(
//                    this,
//                    "Failed to add seat",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//    }
//}

package com.example.libreserve.admin.seats

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.libreserve.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore

class ManageSeatsActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_seats)

        findViewById<com.google.android.material.appbar.MaterialToolbar>(
            R.id.toolbarSeats
        ).setNavigationOnClickListener {
            finish()
        }

        findViewById<MaterialButton>(R.id.btnAddSeat)
            .setOnClickListener {
                saveSeat()
            }
    }

    private fun saveSeat() {

        val seatNumber =
            findViewById<TextInputEditText>(
                R.id.etSeatNumber
            ).text.toString().trim()

        val areaId =
            findViewById<TextInputEditText>(
                R.id.etAreaId
            ).text.toString().trim()

        val libraryId =
            findViewById<TextInputEditText>(
                R.id.etLibraryId
            ).text.toString().trim()

        val status =
            findViewById<TextInputEditText>(
                R.id.etSeatStatus
            ).text.toString().trim()

        val row =
            findViewById<TextInputEditText>(
                R.id.etSeatRow
            ).text.toString().toIntOrNull()

        val col =
            findViewById<TextInputEditText>(
                R.id.etSeatCol
            ).text.toString().toIntOrNull()

        if (
            seatNumber.isBlank() ||
            areaId.isBlank() ||
            libraryId.isBlank() ||
            status.isBlank() ||
            row == null ||
            col == null
        ) {

            Toast.makeText(
                this,
                "Please complete all fields",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val document =
            db.collection("seats").document()

        val seat = hashMapOf(
            "seatId" to document.id,
            "seatNumber" to seatNumber,
            "areaId" to areaId,
            "libraryId" to libraryId,
            "status" to status,
            "row" to row,
            "col" to col
        )

        document.set(seat)
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Seat added successfully",
                    Toast.LENGTH_SHORT
                ).show()

                clearFields()
            }
            .addOnFailureListener { error ->

                Toast.makeText(
                    this,
                    "Failed: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun clearFields() {

        findViewById<TextInputEditText>(
            R.id.etSeatNumber
        ).text?.clear()

        findViewById<TextInputEditText>(
            R.id.etAreaId
        ).text?.clear()

        findViewById<TextInputEditText>(
            R.id.etLibraryId
        ).text?.clear()

        findViewById<TextInputEditText>(
            R.id.etSeatStatus
        ).text?.clear()

        findViewById<TextInputEditText>(
            R.id.etSeatRow
        ).text?.clear()

        findViewById<TextInputEditText>(
            R.id.etSeatCol
        ).text?.clear()
    }
}