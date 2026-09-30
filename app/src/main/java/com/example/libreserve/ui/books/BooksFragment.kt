package com.example.libreserve.ui.books

import android.os.Bundle
import androidx.core.widget.doAfterTextChanged
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.libreserve.R
import com.example.libreserve.adapter.BookAdapter
import com.example.libreserve.databinding.FragmentBooksBinding
import com.example.libreserve.viewmodel.BookViewModel

class BooksFragment : Fragment() {

    private var _binding: FragmentBooksBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BookViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBooksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val adapter = BookAdapter { book ->
            val bundle = Bundle().apply {
                putString("bookId", book.bookId)
            }
            findNavController().navigate(R.id.action_books_to_book_detail, bundle)
        }
        
        binding.recyclerViewBooks.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewBooks.adapter = adapter
        
        fun updateBooks() {
            val query = binding.etSearch.text.toString().trim()
            val books = viewModel.books.value.orEmpty().filter {
                (it.title.contains(query, true) || it.author.contains(query, true)) &&
                    (!binding.chipAvailable.isChecked || it.isAvailable)
            }
            adapter.submitList(books)
            binding.tvResultCount.text = "${books.size} books to explore"
            binding.tvEmptyState.visibility = if (books.isEmpty()) View.VISIBLE else View.GONE
        }
        viewModel.books.observe(viewLifecycleOwner) { updateBooks() }
        binding.etSearch.doAfterTextChanged { updateBooks() }
        binding.chipGroup.setOnCheckedStateChangeListener { _, _ -> updateBooks() }
        
        viewModel.loadBooks()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
