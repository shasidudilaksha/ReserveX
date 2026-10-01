package com.example.libreserve.ui.seats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.graphics.Rect
import com.example.libreserve.utils.UiMotion
import com.example.libreserve.R
import com.example.libreserve.adapter.SeatAdapter
import com.example.libreserve.databinding.FragmentSeatMapBinding
import com.example.libreserve.model.Reservation
import com.example.libreserve.model.ReservationStatus
import com.example.libreserve.model.ReservationType
import com.example.libreserve.model.SeatStatus
import com.example.libreserve.viewmodel.ReservationViewModel
import com.example.libreserve.viewmodel.SeatViewModel
import java.util.UUID

class SeatMapFragment : Fragment() {

    private var _binding: FragmentSeatMapBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SeatViewModel by activityViewModels()
    private val reservationViewModel: ReservationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSeatMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val areaName = arguments?.getString("areaName") ?: "Area"
        binding.textViewAreaName.text = areaName
        binding.tvVisitDetails.text = listOfNotNull(
            arguments?.getString("selectedDate") ?: viewModel.selectedDateStr,
            arguments?.getString("selectedTime") ?: viewModel.selectedTimeSlot
        ).joinToString(" · ")
        UiMotion.enter(binding.headerBar)
        UiMotion.enter(binding.layoutBottomPanel, 100)
        
        val adapter = SeatAdapter { seat ->
            if (seat.status == SeatStatus.AVAILABLE || seat.status == SeatStatus.SELECTED) {
                if (seat.status == SeatStatus.SELECTED) viewModel.deselectSeat() else viewModel.selectSeat(seat)
            }
        }
        
        binding.recyclerViewSeats.layoutManager = GridLayoutManager(requireContext(), 4)
        binding.recyclerViewSeats.adapter = adapter
        binding.recyclerViewSeats.itemAnimator = null
        binding.recyclerViewSeats.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                val position = parent.getChildAdapterPosition(view)
                val aisle = (8 * resources.displayMetrics.density).toInt()
                if (position % 4 == 1) outRect.right = aisle
                if (position % 4 == 2) outRect.left = aisle
            }
        })
        
        viewModel.seats.observe(viewLifecycleOwner) { seats ->
            adapter.submitList(seats.toList())
            val selectedSeats = seats.filter { it.status == SeatStatus.SELECTED }
            val hasSelection = selectedSeats.isNotEmpty()
            binding.buttonReserveSeat.isEnabled = hasSelection
            binding.tvAvailableCount.text = "${seats.count { it.status == SeatStatus.AVAILABLE }} seats available"
            
            if (hasSelection) {
                binding.textViewSelectedInfo.text = "Selected: ${selectedSeats.joinToString { it.seatNumber }}"
                binding.buttonReserveSeat.text = "Reserve seat ${selectedSeats.first().seatNumber}"
            } else {
                binding.textViewSelectedInfo.text = "Tap an available seat to make it yours."
                binding.buttonReserveSeat.text = "Reserve this seat"
            }
        }
        
        viewModel.loadSeats(areaName)
        
        binding.buttonReserveSeat.setOnClickListener {
            val selectedSeats = viewModel.seats.value?.filter { it.status == SeatStatus.SELECTED }
            if (!selectedSeats.isNullOrEmpty()) {
                val seatNames = selectedSeats.joinToString { it.seatNumber }
                
                val dateStr = viewModel.selectedDateStr ?: "Unknown Date"
                val (startStr, endStr) = when(viewModel.selectedTimeSlot) {
                    "Morning" -> "09:00 AM" to "12:00 PM"
                    "Afternoon" -> "12:00 PM" to "03:00 PM"
                    "Evening" -> "03:00 PM" to "06:00 PM"
                    else -> "09:00 AM" to "12:00 PM"
                }
                
                val resourceName = "$areaName - Seat(s) $seatNames"
                
                val reservation = Reservation(
                    reservationId = UUID.randomUUID().toString(),
                    userId = com.example.libreserve.utils.SessionManager(requireContext()).userId,
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
                reservationViewModel.addReservation(reservation, com.example.libreserve.utils.SessionManager(requireContext()).userId)
                
                val bundle = Bundle().apply {
                    putString("reservationType", ReservationType.SEAT.name)
                    putString("resourceName", resourceName)
                    putString("dateStr", dateStr)
                    putString("startTime", startStr)
                    putString("endTime", endStr)
                    putString("libraryName", "SLIIT Malabe Library")
                    putString("locationStr", areaName)
                }
                findNavController().navigate(R.id.action_seat_map_to_confirm, bundle)
            }
        }
    }

    override fun onDestroyView() {
        binding.headerBar.animate().cancel()
        binding.layoutBottomPanel.animate().cancel()
        binding.recyclerViewSeats.adapter = null
        super.onDestroyView()
        _binding = null
    }
}
