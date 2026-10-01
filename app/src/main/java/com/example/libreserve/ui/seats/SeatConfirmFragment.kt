package com.example.libreserve.ui.seats

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.libreserve.R
import com.example.libreserve.databinding.FragmentSeatConfirmBinding
import com.example.libreserve.model.Reservation
import com.example.libreserve.model.ReservationStatus
import com.example.libreserve.model.ReservationType
import com.example.libreserve.viewmodel.ReservationViewModel
import java.text.SimpleDateFormat
import java.util.*

class SeatConfirmFragment : Fragment() {

    private var _binding: FragmentSeatConfirmBinding? = null
    private val binding get() = _binding!!
    private val reservationViewModel: ReservationViewModel by activityViewModels()
    
    private var selectedDate = Calendar.getInstance()
    private var startTime = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 9); set(Calendar.MINUTE, 0) }
    private var endTime = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 12); set(Calendar.MINUTE, 0) }
    
    private val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSeatConfirmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val areaName = arguments?.getString("areaName") ?: ""
        val seatNames = arguments?.getString("seatNames") ?: ""
        
        updateSummary(areaName, seatNames)
        
        binding.btnSelectDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    selectedDate.set(Calendar.YEAR, year)
                    selectedDate.set(Calendar.MONTH, month)
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    updateSummary(areaName, seatNames)
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        
        binding.btnSelectStart.setOnClickListener {
            TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    startTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    startTime.set(Calendar.MINUTE, minute)
                    updateSummary(areaName, seatNames)
                },
                startTime.get(Calendar.HOUR_OF_DAY),
                startTime.get(Calendar.MINUTE),
                false
            ).show()
        }
        
        binding.btnSelectEnd.setOnClickListener {
            TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    endTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    endTime.set(Calendar.MINUTE, minute)
                    updateSummary(areaName, seatNames)
                },
                endTime.get(Calendar.HOUR_OF_DAY),
                endTime.get(Calendar.MINUTE),
                false
            ).show()
        }
        
        binding.buttonConfirmSeat.setOnClickListener {
            val dateStr = dateFormat.format(selectedDate.time)
            val startStr = timeFormat.format(startTime.time)
            val endStr = timeFormat.format(endTime.time)
            
            val reservation = Reservation(
                reservationId = UUID.randomUUID().toString(),
                userId = com.example.libreserve.utils.SessionManager(requireContext()).userId,
                type = ReservationType.SEAT,
                resourceId = "seat_$seatNames",
                resourceName = "$areaName - Seat(s) $seatNames",
                libraryId = "lib001",
                libraryName = "SLIIT Malabe Library",
                date = dateStr,
                startTime = startStr,
                endTime = endStr,
                status = ReservationStatus.UPCOMING,
                location = areaName
            )
            reservationViewModel.addReservation(reservation, com.example.libreserve.utils.SessionManager(requireContext()).userId)
            
            val bundle = Bundle().apply {
                putString("reservationType", ReservationType.SEAT.name)
                putString("resourceName", "$areaName - Seat(s) $seatNames")
                putString("dateStr", dateStr)
                putString("startTime", startStr)
                putString("endTime", endStr)
                putString("libraryName", "SLIIT Malabe Library")
                putString("locationStr", areaName)
            }
            findNavController().navigate(R.id.action_seat_confirm_to_confirmation, bundle)
        }
    }
    
    private fun updateSummary(areaName: String, seatNames: String) {
        binding.textViewSeatSummary.text = "Location: $areaName\nSeats: $seatNames\nDate: ${dateFormat.format(selectedDate.time)}\nTime: ${timeFormat.format(startTime.time)} - ${timeFormat.format(endTime.time)}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
