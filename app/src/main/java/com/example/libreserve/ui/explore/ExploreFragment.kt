package com.example.libreserve.ui.explore

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.libreserve.R
import com.example.libreserve.adapter.ExploreAdapter
import com.example.libreserve.adapter.ExploreItem
import com.example.libreserve.databinding.FragmentExploreBinding
import com.example.libreserve.viewmodel.ExploreViewModel

class ExploreFragment : Fragment() {
    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExploreViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val adapter = ExploreAdapter { item ->
            when (item) {
                is ExploreItem.BookItem -> {
                    val bundle = Bundle().apply { putString("bookId", item.book.bookId) }
                    findNavController().navigate(R.id.action_explore_to_books, bundle)
                }
                is ExploreItem.AreaItem -> {
                    val bundle = Bundle().apply {
                        putString("areaId", item.area.areaId)
                        putString("areaName", item.area.name)
                        putString("libraryId", item.area.libraryId)
                    }
                    findNavController().navigate(R.id.action_explore_to_seats, bundle)
                }
                is ExploreItem.RoomItem -> {
                    val bundle = Bundle().apply {
                        putString("roomId", item.room.roomId)
                        putString("roomName", item.room.name)
                    }
                    findNavController().navigate(R.id.action_explore_to_rooms, bundle)
                }
            }
        }
        
        binding.rvResults.layoutManager = LinearLayoutManager(requireContext())
        binding.rvResults.adapter = adapter
        
        viewModel.books.observe(viewLifecycleOwner) { updateList(adapter) }
        viewModel.areas.observe(viewLifecycleOwner) { updateList(adapter) }
        viewModel.rooms.observe(viewLifecycleOwner) { updateList(adapter) }
        
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.search(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        
        binding.chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            val filter = when (checkedIds.firstOrNull()) {
                R.id.chipBooks -> "BOOKS"
                R.id.chipSeats -> "SEATS"
                R.id.chipRooms -> "ROOMS"
                else -> "ALL"
            }
            viewModel.filterByType(filter)
        }
        
        viewModel.loadInitial()
    }
    
    private fun updateList(adapter: ExploreAdapter) {
        val list = mutableListOf<ExploreItem>()
        viewModel.books.value?.forEach { list.add(ExploreItem.BookItem(it)) }
        viewModel.areas.value?.forEach { list.add(ExploreItem.AreaItem(it)) }
        viewModel.rooms.value?.forEach { list.add(ExploreItem.RoomItem(it)) }
        adapter.submitList(list)
        binding.tvResultCount.text = "${list.size} resources to explore"
        binding.tvEmptyState.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
