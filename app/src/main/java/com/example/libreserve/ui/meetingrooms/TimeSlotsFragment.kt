package com.example.libreserve.ui.meetingrooms

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.libreserve.R
import com.example.libreserve.adapter.TimeSlotAdapter
import com.example.libreserve.databinding.FragmentTimeSlotsBinding
import com.example.libreserve.model.SlotStatus
import com.example.libreserve.viewmodel.MeetingRoomViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TimeSlotsFragment : Fragment() {

    private var _binding: FragmentTimeSlotsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MeetingRoomViewModel by activityViewModels()

    private var selectedDate = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimeSlotsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val roomId = arguments?.getString("roomId") ?: return
        val roomName = arguments?.getString("roomName").orEmpty().ifBlank {
            viewModel.getRoomById(roomId)?.name ?: "Meeting Room"
        }

        binding.textViewRoomHeader.text = roomName
        updateDateButtonText()
        viewModel.deselectSlot()

        val adapter = TimeSlotAdapter { slot ->
            if (slot.status == SlotStatus.AVAILABLE) {
                viewModel.selectSlot(slot)
            }
        }

        binding.recyclerViewTimeSlots.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewTimeSlots.adapter = adapter

        viewModel.timeSlots.observe(viewLifecycleOwner) { slots ->
            adapter.submitList(slots)
        }
        viewModel.selectedSlot.observe(viewLifecycleOwner) { selectedSlot ->
            adapter.setSelectedSlot(selectedSlot?.slotId)
            binding.buttonProceed.isEnabled = selectedSlot != null
            binding.textViewSelectedSlot.text = selectedSlot?.let {
                "${it.startTime} - ${it.endTime} selected"
            } ?: "Select a time slot to continue"
        }

        viewModel.loadTimeSlots(roomId, dateFormat.format(selectedDate.time))

        binding.buttonDateSelect.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    selectedDate.set(Calendar.YEAR, year)
                    selectedDate.set(Calendar.MONTH, month)
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    updateDateButtonText()
                    viewModel.deselectSlot()
                    viewModel.loadTimeSlots(roomId, dateFormat.format(selectedDate.time))
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.buttonProceed.setOnClickListener {
            val selectedSlot = viewModel.selectedSlot.value
            if (selectedSlot != null) {
                val bundle = Bundle().apply {
                    putString("roomId", roomId)
                    putString("roomName", roomName)
                    putString("slotId", selectedSlot.slotId)
                    putString("timeSlot", "${selectedSlot.startTime} - ${selectedSlot.endTime}")
                    putString("startTime", selectedSlot.startTime)
                    putString("endTime", selectedSlot.endTime)
                    putString("selectedDate", dateFormat.format(selectedDate.time))
                }
                findNavController().navigate(R.id.action_time_slots_to_room_confirm, bundle)
            }
        }
    }

    private fun updateDateButtonText() {
        binding.tvSelectedDate.text = dateFormat.format(selectedDate.time)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
