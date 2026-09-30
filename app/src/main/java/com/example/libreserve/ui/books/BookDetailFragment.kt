package com.example.libreserve.ui.books

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.libreserve.R
import com.example.libreserve.databinding.FragmentBookDetailBinding
import com.example.libreserve.viewmodel.BookViewModel

class BookDetailFragment : Fragment() {

    private var _binding: FragmentBookDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BookViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bookId = arguments?.getString("bookId") ?: return
        
        viewModel.getBook(bookId)?.let { book ->
            binding.textViewTitle.text = book.title
            binding.textViewAuthor.text = book.author
            binding.textViewDescription.text = book.description
            binding.buttonReserve.isEnabled = book.isAvailable
            binding.tvAvailability.text = if (book.isAvailable) "${book.availableCopies} copies available" else "Currently unavailable"
            binding.tvShelf.text = "Shelf ${book.shelfLocation}"
            if (!book.isAvailable) binding.buttonReserve.text = "Currently unavailable"
            
            binding.buttonReserve.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("bookId", book.bookId)
                    putString("bookTitle", book.title)
                }
                findNavController().navigate(R.id.action_book_detail_to_reservation, bundle)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
