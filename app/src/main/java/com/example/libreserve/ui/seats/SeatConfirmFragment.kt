package com.example.libreserve.ui.seats

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
import com.example.libreserve.utils.SessionManager
import com.example.libreserve.viewmodel.ReservationViewModel
import java.util.UUID

class SeatConfirmFragment : Fragment() {

    private var _binding: FragmentSeatConfirmBinding? = null
    private val binding get() = _binding!!
    private val reservationViewModel: ReservationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSeatConfirmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val areaName = arguments?.getString("areaName") ?: "Quiet Zone"
        val seatNames = arguments?.getString("seatNames") ?: ""
        val dateStr = arguments?.getString("selectedDate") ?: "Today"
        val timeSlot = arguments?.getString("timeSlot") ?: "Morning"
        val startStr = arguments?.getString("startTime") ?: "09:00 AM"
        val endStr = arguments?.getString("endTime") ?: "12:00 PM"

        binding.tvConfirmSeatTitle.text = areaName
        binding.tvConfirmSeatSummary.text = if (seatNames.isNotBlank()) "Seat $seatNames" else "Seat Selected"
        binding.tvConfirmSeatDate.text = dateStr
        binding.tvConfirmSeatTime.text = "$timeSlot ($startStr – $endStr)"
        binding.tvConfirmSeatLocation.text = "SLIIT Malabe Library, $areaName"

        binding.buttonConfirmSeat.setOnClickListener {
            val resourceName = "$areaName - Seat $seatNames"
            val reservation = Reservation(
                reservationId = UUID.randomUUID().toString(),
                userId = SessionManager(requireContext()).userId,
                type = ReservationType.SEAT,
                resourceId = "seat_$seatNames",
                resourceName = resourceName,
                libraryId = "lib001",
                libraryName = "SLIIT Malabe Library",
                date = dateStr,
                startTime = startStr,
                endTime = endStr,
                status = ReservationStatus.UPCOMING,
                location = areaName
            )
            reservationViewModel.addReservation(reservation, SessionManager(requireContext()).userId)

            val bundle = Bundle().apply {
                putString("reservationType", ReservationType.SEAT.name)
                putString("resourceName", resourceName)
                putString("dateStr", dateStr)
                putString("startTime", startStr)
                putString("endTime", endStr)
                putString("libraryName", "SLIIT Malabe Library")
                putString("locationStr", areaName)
            }
            findNavController().navigate(R.id.action_seat_confirm_to_confirmation, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
