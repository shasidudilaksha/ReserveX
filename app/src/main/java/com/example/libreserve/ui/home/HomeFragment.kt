package com.example.libreserve.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.libreserve.R
import com.example.libreserve.databinding.FragmentHomeBinding
import com.example.libreserve.model.ReservationType
import com.example.libreserve.utils.SessionManager
import com.example.libreserve.utils.UiMotion
import java.util.Calendar
import com.example.libreserve.viewmodel.ReservationViewModel

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val reservationViewModel: ReservationViewModel by activityViewModels()
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())
        setupViews()
        observeData()
        UiMotion.enter(binding.libraryHero)
        UiMotion.enter(binding.searchBar, 80)
        UiMotion.enter(binding.servicesRow, 140)
    }

    private fun setupViews() {
        binding.apply {
            val firstName = sessionManager.userName.split(" ").firstOrNull() ?: "Alex"
            val greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
                in 5..11 -> "Good morning"
                in 12..16 -> "Good afternoon"
                else -> "Good evening"
            }
            tvGreeting.text = "$greeting, $firstName"
            btnFindSeat.setOnClickListener {
                findNavController().navigate(R.id.action_home_to_seats)
            }
            upcomingReservationCard.setOnClickListener {
                findNavController().navigate(R.id.reservationsFragment)
            }
            
            btnNotifications.setOnClickListener {
                findNavController().navigate(R.id.action_home_to_notifications)
            }
            searchBar.setOnClickListener {
                findNavController().navigate(R.id.exploreFragment)
            }
            cardBooks.setOnClickListener {
                findNavController().navigate(R.id.action_home_to_books)
            }
            cardSeats.setOnClickListener {
                findNavController().navigate(R.id.action_home_to_seats)
            }
            cardRooms.setOnClickListener {
                findNavController().navigate(R.id.action_home_to_meeting_rooms)
            }
        }
    }
    
    private fun observeData() {
        reservationViewModel.upcomingReservations.observe(viewLifecycleOwner) { reservations ->
            val upcoming = reservations.firstOrNull()
            if (upcoming != null) {
                binding.upcomingReservationCard.visibility = View.VISIBLE
                binding.emptyReservationState.visibility = View.GONE
                binding.upcomingTitle.text = upcoming.resourceName
                binding.upcomingType.text = when(upcoming.type) {
                    ReservationType.BOOK -> "Book Pick-up"
                    ReservationType.SEAT -> "Reading Seat"
                    ReservationType.MEETING_ROOM -> "Meeting Room"
                }
                binding.upcomingDate.text = "${upcoming.date}, ${upcoming.startTime}"
            } else {
                binding.upcomingReservationCard.visibility = View.GONE
                binding.emptyReservationState.visibility = View.VISIBLE
            }
        }
        reservationViewModel.loadReservations(com.example.libreserve.utils.SessionManager(requireContext()).userId)
    }

    override fun onDestroyView() {
        listOf(binding.libraryHero, binding.searchBar, binding.servicesRow).forEach { it.animate().cancel() }
        super.onDestroyView()
        _binding = null
    }
}
