package com.example.libreserve.ui.confirmation

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.libreserve.R
import com.example.libreserve.databinding.FragmentConfirmationBinding
import com.example.libreserve.model.ReservationType

class ConfirmationFragment : Fragment() {
    private var _binding: FragmentConfirmationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        arguments?.let {
            val resourceName = it.getString("resourceName", "")
            val date = it.getString("dateStr", "")
            val startTime = it.getString("startTime", "")
            val endTime = it.getString("endTime", "")
            val libraryName = it.getString("libraryName", "")
            val location = it.getString("locationStr", "")
            val reservationType = runCatching {
                ReservationType.valueOf(it.getString("reservationType", ""))
            }.getOrNull()

            binding.tvResourceName.text = resourceName
            binding.tvDate.text = date
            binding.tvTime.text = "$startTime - $endTime"
            binding.tvLocation.text = "$libraryName, $location"
            applyReservationStyle(reservationType)
            binding.tvPassCode.text = buildPassCode(reservationType, resourceName, date, startTime)
        }

        binding.btnHome.setOnClickListener {
            findNavController().popBackStack(R.id.homeFragment, false)
        }

        binding.btnViewReservations.setOnClickListener {
            findNavController().navigate(R.id.action_confirmation_to_reservations)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun applyReservationStyle(type: ReservationType?) {
        val context = requireContext()
        val style = when (type) {
            ReservationType.BOOK -> ConfirmationStyle(
                title = "Book Reserved!",
                subtitle = "Your pickup pass is ready.",
                typeLabel = "Book Pickup Pass",
                note = "Show this at the circulation desk when collecting your book.",
                iconRes = R.drawable.ic_book,
                iconColorRes = R.color.colorPrimary,
                containerColorRes = R.color.colorLightBlue
            )
            ReservationType.SEAT -> ConfirmationStyle(
                title = "Seat Reserved!",
                subtitle = "Your study desk is secured.",
                typeLabel = "Seat Reservation Pass",
                note = "Use this pass when checking in at the study area.",
                iconRes = R.drawable.ic_seat,
                iconColorRes = R.color.colorSelected,
                containerColorRes = R.color.colorLightOrange
            )
            ReservationType.MEETING_ROOM -> ConfirmationStyle(
                title = "Room Booked!",
                subtitle = "Your room is ready for collaboration.",
                typeLabel = "Room Booking Pass",
                note = "Keep this pass handy when you arrive at the meeting room.",
                iconRes = R.drawable.ic_meeting_room,
                iconColorRes = R.color.colorRoomsFeature,
                containerColorRes = R.color.colorLightGreen
            )
            null -> ConfirmationStyle(
                title = "Booking Confirmed!",
                subtitle = "Your reservation pass is ready.",
                typeLabel = "Reservation Pass",
                note = "Keep this pass handy when you arrive.",
                iconRes = R.drawable.ic_check_circle,
                iconColorRes = R.color.colorSuccess,
                containerColorRes = R.color.colorLightGreen
            )
        }

        binding.tvConfirmationTitle.text = style.title
        binding.tvConfirmationSubtitle.text = style.subtitle
        binding.tvReservationTypeLabel.text = style.typeLabel
        binding.tvConfirmationNote.text = style.note
        binding.iconReservationType.setImageResource(style.iconRes)
        binding.iconReservationType.setColorFilter(ContextCompat.getColor(context, style.iconColorRes))
        binding.iconReservationContainer.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(context, style.containerColorRes))
    }

    private fun buildPassCode(
        type: ReservationType?,
        resourceName: String,
        date: String,
        startTime: String
    ): String {
        val prefix = when (type) {
            ReservationType.BOOK -> "BK"
            ReservationType.SEAT -> "ST"
            ReservationType.MEETING_ROOM -> "RM"
            null -> "RX"
        }
        val hash = "$resourceName$date$startTime".hashCode().toLong() and 0x7fffffffL
        val passNumber = (hash % 9000L) + 1000L
        return "$prefix-$passNumber"
    }

    private data class ConfirmationStyle(
        val title: String,
        val subtitle: String,
        val typeLabel: String,
        val note: String,
        val iconRes: Int,
        val iconColorRes: Int,
        val containerColorRes: Int
    )
}
