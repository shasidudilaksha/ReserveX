package com.example.libreserve.admin.books

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.libreserve.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore

class EditBookActivity : AppCompatActivity() {

    private val db =
        FirebaseFirestore.getInstance()

    private lateinit var bookId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_edit_book)

        bookId =
            intent.getStringExtra("BOOK_ID")
                .orEmpty()

        if (bookId.isBlank()) {
            finish()
            return
        }

        loadBook()

        findViewById<MaterialButton>(R.id.btnSaveBook)
            .setOnClickListener {
                updateBook()
            }
    }

    private fun loadBook() {

        db.collection("books")
            .document(bookId)
            .get()
            .addOnSuccessListener {

                val book =
                    it.toObject(AdminBook::class.java)
                        ?: return@addOnSuccessListener

                setValue(R.id.etTitle, book.title)
                setValue(R.id.etAuthor, book.author)
                setValue(R.id.etCategory, book.category)
                setValue(R.id.etIsbn, book.isbn)
                setValue(R.id.etShelf, book.shelfLocation)
                setValue(
                    R.id.etDescription,
                    book.description
                )

                setValue(
                    R.id.etTotalCopies,
                    book.totalCopies.toString()
                )

                setValue(
                    R.id.etAvailableCopies,
                    book.availableCopies.toString()
                )
            }
    }

    private fun updateBook() {

        val total =
            value(R.id.etTotalCopies)
                .toIntOrNull()
                ?: return

        val available =
            value(R.id.etAvailableCopies)
                .toIntOrNull()
                ?: return

        if (available > total)
            return

        val updates = hashMapOf<String, Any>(
            "title" to value(R.id.etTitle),
            "author" to value(R.id.etAuthor),
            "category" to value(R.id.etCategory),
            "isbn" to value(R.id.etIsbn),
            "shelfLocation" to value(R.id.etShelf),
            "description" to value(R.id.etDescription),
            "totalCopies" to total,
            "availableCopies" to available
        )

        db.collection("books")
            .document(bookId)
            .update(updates)
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Book updated",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
    }

    private fun value(id: Int) =
        findViewById<TextInputEditText>(id)
            .text
            ?.toString()
            ?.trim()
            .orEmpty()

    private fun setValue(
        id: Int,
        value: String
    ) {

        findViewById<TextInputEditText>(id)
            .setText(value)
    }
}