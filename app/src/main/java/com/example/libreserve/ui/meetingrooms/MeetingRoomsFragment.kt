package com.example.libreserve.ui.meetingrooms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.libreserve.R
import com.example.libreserve.adapter.MeetingRoomAdapter
import com.example.libreserve.databinding.FragmentMeetingRoomsBinding
import com.example.libreserve.viewmodel.MeetingRoomViewModel

class MeetingRoomsFragment : Fragment() {

    private var _binding: FragmentMeetingRoomsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MeetingRoomViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMeetingRoomsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val adapter = MeetingRoomAdapter { room ->
            val bundle = Bundle().apply {
                putString("roomId", room.roomId)
                putString("roomName", room.name)
            }
            findNavController().navigate(R.id.action_rooms_to_time_slots, bundle)
        }
        
        binding.recyclerViewRooms.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewRooms.adapter = adapter
        
        viewModel.rooms.observe(viewLifecycleOwner) { rooms ->
            adapter.submitList(rooms)
        }
        
        viewModel.loadRooms("lib001")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
