package com.example.libreserve.admin.books

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.libreserve.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore

class ManageBooksActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    private val allBooks = mutableListOf<AdminBook>()

    private lateinit var adapter: AdminBookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_books)

        findViewById<com.google.android.material.appbar.MaterialToolbar>(
            R.id.toolbar
        ).setNavigationOnClickListener {
            finish()
        }

        adapter = AdminBookAdapter(
            books = emptyList(),

            onEdit = { book ->

                val intent =
                    Intent(this, EditBookActivity::class.java)

                intent.putExtra("BOOK_ID", book.bookId)

                startActivity(intent)
            },

            onDelete = { book ->
                confirmDelete(book)
            }
        )

        val recyclerView =
            findViewById<RecyclerView>(R.id.rvBooks)

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        recyclerView.adapter = adapter

        findViewById<MaterialButton>(R.id.btnAddBook)
            .setOnClickListener {

                startActivity(
                    Intent(
                        this,
                        AddBookActivity::class.java
                    )
                )
            }

        findViewById<TextInputEditText>(R.id.etSearch)
            .doOnTextChanged { text, _, _, _ ->

                filterBooks(
                    text?.toString().orEmpty()
                )
            }
    }

    override fun onResume() {
        super.onResume()
        loadBooks()
    }

    private fun loadBooks() {

        db.collection("books")
            .get()
            .addOnSuccessListener { result ->

                allBooks.clear()

                for (document in result.documents) {

                    val book =
                        document.toObject(AdminBook::class.java)

                    if (book != null) {

                        book.bookId = document.id
                        allBooks.add(book)
                    }
                }

                adapter.updateData(allBooks)
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Unable to load books",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun filterBooks(query: String) {

        if (query.isBlank()) {
            adapter.updateData(allBooks)
            return
        }

        val result = allBooks.filter {

            it.title.contains(
                query,
                ignoreCase = true
            ) ||
                    it.author.contains(
                        query,
                        ignoreCase = true
                    ) ||
                    it.isbn.contains(
                        query,
                        ignoreCase = true
                    )
        }

        adapter.updateData(result)
    }

    private fun confirmDelete(book: AdminBook) {

        AlertDialog.Builder(this)
            .setTitle("Delete Book")
            .setMessage(
                "Delete \"${book.title}\"?"
            )
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ ->

                db.collection("books")
                    .document(book.bookId)
                    .delete()
                    .addOnSuccessListener {
                        loadBooks()
                    }
            }
            .show()
    }
}