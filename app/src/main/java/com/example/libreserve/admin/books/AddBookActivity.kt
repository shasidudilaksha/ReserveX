package com.example.libreserve.admin.books

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.libreserve.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore

class AddBookActivity : AppCompatActivity() {

    private val db =
        FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_book)

        findViewById<MaterialButton>(R.id.btnSaveBook)
            .setOnClickListener {
                saveBook()
            }
    }

    private fun saveBook() {

        val title =
            value(R.id.etTitle)

        val author =
            value(R.id.etAuthor)

        val category =
            value(R.id.etCategory)

        val isbn =
            value(R.id.etIsbn)

        val shelf =
            value(R.id.etShelf)

        val description =
            value(R.id.etDescription)

        val total =
            value(R.id.etTotalCopies)
                .toIntOrNull()

        val available =
            value(R.id.etAvailableCopies)
                .toIntOrNull()

        if (
            title.isBlank() ||
            author.isBlank() ||
            total == null ||
            available == null
        ) {

            Toast.makeText(
                this,
                "Complete all required fields.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }



        if (available > total) {

            Toast.makeText(
                this,
                "Available copies cannot exceed total copies.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val document =
            db.collection("books").document()

        val book = AdminBook(
            bookId = document.id,
            title = title,
            author = author,
            category = category,
            isbn = isbn,
            description = description,
            shelfLocation = shelf,
            totalCopies = total,
            availableCopies = available
        )

//        db.collection("books")
//            .document()
//            .set(book)

        document.set(book)
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Book added successfully",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    it.message ?: "Unable to add book",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun value(id: Int): String {

        return findViewById<TextInputEditText>(id)
            .text
            ?.toString()
            ?.trim()
            .orEmpty()
    }


}