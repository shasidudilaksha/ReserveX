package com.example.libreserve.ui.seats

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.libreserve.R
import com.example.libreserve.databinding.FragmentSeatsBinding
import com.example.libreserve.viewmodel.SeatViewModel
import java.text.SimpleDateFormat
import java.util.*

class SeatsFragment : Fragment() {

    private var _binding: FragmentSeatsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SeatViewModel by activityViewModels()
    
    private var selectedDate = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSeatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize Date if not set
        if (viewModel.selectedDateStr == null) {
            viewModel.selectedDateStr = dateFormat.format(selectedDate.time)
        }
        binding.tvSelectedDate.text = "🗓  " + viewModel.selectedDateStr
        
        // Date Picker
        binding.buttonDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    selectedDate.set(Calendar.YEAR, year)
                    selectedDate.set(Calendar.MONTH, month)
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    viewModel.selectedDateStr = dateFormat.format(selectedDate.time)
                    binding.tvSelectedDate.text = "🗓  " + viewModel.selectedDateStr
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Initialize UI for time slots
        updateTimeSlotUI(viewModel.selectedTimeSlot)

        binding.cardMorning.setOnClickListener {
            viewModel.selectedTimeSlot = "Morning"
            updateTimeSlotUI("Morning")
        }

        binding.cardAfternoon.setOnClickListener {
            viewModel.selectedTimeSlot = "Afternoon"
            updateTimeSlotUI("Afternoon")
        }

        binding.cardEvening.setOnClickListener {
            viewModel.selectedTimeSlot = "Evening"
            updateTimeSlotUI("Evening")
        }
        
        val areaClickListener = View.OnClickListener { v ->
            if (viewModel.selectedTimeSlot == null) {
                Toast.makeText(requireContext(), "Please select a time period first", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            
            val areaName = when(v.id) {
                R.id.cardAreaQuietA -> "Quiet Zone: Section A"
                R.id.cardAreaQuietB -> "Quiet Zone: Section B"
                R.id.cardAreaQuietC -> "Quiet Zone: Section C"
                else -> "Quiet Zone"
            }
            
            val bundle = Bundle().apply {
                putString("areaName", areaName)
            }
            findNavController().navigate(R.id.action_seats_to_seat_map, bundle)
        }
        
        binding.cardAreaQuietA.setOnClickListener(areaClickListener)
        binding.cardAreaQuietB.setOnClickListener(areaClickListener)
        binding.cardAreaQuietC.setOnClickListener(areaClickListener)
    }
    
    private fun updateTimeSlotUI(selected: String?) {
        resetSlot(binding.cardMorning, binding.pillMorning, binding.tvPillMorning, binding.ivPillMorning)
        resetSlot(binding.cardAfternoon, binding.pillAfternoon, binding.tvPillAfternoon, binding.ivPillAfternoon)
        resetSlot(binding.cardEvening, binding.pillEvening, binding.tvPillEvening, binding.ivPillEvening)
        
        when (selected) {
            "Morning" -> selectSlot(binding.cardMorning, binding.pillMorning, binding.tvPillMorning, binding.ivPillMorning)
            "Afternoon" -> selectSlot(binding.cardAfternoon, binding.pillAfternoon, binding.tvPillAfternoon, binding.ivPillAfternoon)
            "Evening" -> selectSlot(binding.cardEvening, binding.pillEvening, binding.tvPillEvening, binding.ivPillEvening)
        }
    }
    
    private fun resetSlot(card: com.google.android.material.card.MaterialCardView, pill: com.google.android.material.card.MaterialCardView, text: android.widget.TextView, icon: android.widget.ImageView) {
        card.setCardBackgroundColor(Color.WHITE)
        card.strokeWidth = 0
        pill.setCardBackgroundColor(Color.parseColor("#E8F5E9"))
        text.text = "Available"
        text.setTextColor(Color.parseColor("#4CAF50"))
        icon.visibility = View.GONE
    }

    private fun selectSlot(card: com.google.android.material.card.MaterialCardView, pill: com.google.android.material.card.MaterialCardView, text: android.widget.TextView, icon: android.widget.ImageView) {
        card.setCardBackgroundColor(Color.parseColor("#F0F2FF"))
        card.strokeWidth = 4 // equivalent to 2dp roughly depending on density
        card.strokeColor = Color.parseColor("#3F51B5")
        pill.setCardBackgroundColor(Color.parseColor("#3F51B5"))
        text.text = "Selected"
        text.setTextColor(Color.WHITE)
        icon.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
