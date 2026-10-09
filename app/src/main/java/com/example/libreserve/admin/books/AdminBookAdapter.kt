package com.example.libreserve.admin.books

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.libreserve.R
import com.google.android.material.button.MaterialButton

class AdminBookAdapter(
    private var books: List<AdminBook>,
    private val onEdit: (AdminBook) -> Unit,
    private val onDelete: (AdminBook) -> Unit
) : RecyclerView.Adapter<AdminBookAdapter.BookViewHolder>() {

    class BookViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val title: TextView = view.findViewById(R.id.tvTitle)
        val author: TextView = view.findViewById(R.id.tvAuthor)
        val availability: TextView =
            view.findViewById(R.id.tvAvailability)

        val editButton: MaterialButton =
            view.findViewById(R.id.btnEdit)

        val deleteButton: MaterialButton =
            view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BookViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_admin_book,
                parent,
                false
            )

        return BookViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: BookViewHolder,
        position: Int
    ) {

        val book = books[position]

        holder.title.text = book.title
        holder.author.text =
            "${book.author} • ${book.category}"

        holder.availability.text =
            "${book.availableCopies}/${book.totalCopies} copies available"

        holder.editButton.setOnClickListener {
            onEdit(book)
        }

        holder.deleteButton.setOnClickListener {
            onDelete(book)
        }
    }

    override fun getItemCount() = books.size

    fun updateData(newBooks: List<AdminBook>) {
        books = newBooks
        notifyDataSetChanged()
    }
}