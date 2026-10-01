package com.example.libreserve.ui.meetingrooms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.libreserve.R
import com.example.libreserve.databinding.FragmentRoomConfirmBinding
import com.example.libreserve.model.Reservation
import com.example.libreserve.model.ReservationStatus
import com.example.libreserve.model.ReservationType
import com.example.libreserve.viewmodel.MeetingRoomViewModel
import com.example.libreserve.viewmodel.ReservationViewModel
import java.util.UUID

class RoomConfirmFragment : Fragment() {

    private var _binding: FragmentRoomConfirmBinding? = null
    private val binding get() = _binding!!
    private val meetingRoomViewModel: MeetingRoomViewModel by activityViewModels()
    private val reservationViewModel: ReservationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoomConfirmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val roomId = arguments?.getString("roomId") ?: ""
        val room = meetingRoomViewModel.getRoomById(roomId)
        val roomName = arguments?.getString("roomName").orEmpty().ifBlank {
            room?.name ?: "Meeting Room"
        }
        val timeSlot = arguments?.getString("timeSlot") ?: ""
        val startTime = arguments?.getString("startTime") ?: ""
        val endTime = arguments?.getString("endTime") ?: ""
        val selectedDate = arguments?.getString("selectedDate") ?: ""
        val location = "Floor ${room?.floor ?: 2}"
        val roomSummary = room?.let {
            "Capacity ${it.capacity} - ${it.equipment.joinToString(" - ")}"
        } ?: "Room booking details confirmed"

        binding.textViewRoomTitle.text = roomName
        binding.textViewRoomSummary.text = roomSummary
        binding.textViewRoomDate.text = selectedDate
        binding.textViewRoomTime.text = timeSlot
        binding.textViewRoomLocation.text = location

        binding.buttonConfirmRoom.setOnClickListener {
            val reservation = Reservation(
                reservationId = UUID.randomUUID().toString(),
                userId = com.example.libreserve.utils.SessionManager(requireContext()).userId,
                type = ReservationType.MEETING_ROOM,
                resourceId = roomId,
                resourceName = roomName,
                libraryId = "lib001",
                libraryName = "SLIIT Malabe Library",
                date = selectedDate,
                startTime = startTime,
                endTime = endTime,
                status = ReservationStatus.UPCOMING,
                location = location
            )
            reservationViewModel.addReservation(reservation, com.example.libreserve.utils.SessionManager(requireContext()).userId)

            val bundle = Bundle().apply {
                putString("reservationType", ReservationType.MEETING_ROOM.name)
                putString("resourceName", roomName)
                putString("dateStr", selectedDate)
                putString("startTime", startTime)
                putString("endTime", endTime)
                putString("libraryName", "SLIIT Malabe Library")
                putString("locationStr", location)
            }
            findNavController().navigate(R.id.action_room_confirm_to_confirmation, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
