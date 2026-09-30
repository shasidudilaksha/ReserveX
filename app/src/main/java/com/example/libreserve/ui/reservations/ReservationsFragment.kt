package com.example.libreserve.ui.reservations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.libreserve.R
import com.example.libreserve.adapter.ReservationAdapter
import com.example.libreserve.databinding.FragmentReservationsBinding
import com.example.libreserve.viewmodel.ReservationViewModel
import com.example.libreserve.model.ReservationStatus
import com.google.android.material.tabs.TabLayout

class ReservationsFragment : Fragment() {
    private var _binding: FragmentReservationsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ReservationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReservationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val adapter = ReservationAdapter(
            onCancelClick = { reservation ->
                viewModel.cancelReservation(reservation.reservationId, "user001")
                Toast.makeText(requireContext(), "Reservation cancelled", Toast.LENGTH_SHORT).show()
            },
            onModifyClick = { reservation ->
                val bundle = Bundle().apply {
                    putString("reservationId", reservation.reservationId)
                }
                findNavController().navigate(R.id.action_reservations_to_modify, bundle)
            }
        )
        
        binding.rvReservations.layoutManager = LinearLayoutManager(requireContext())
        binding.rvReservations.adapter = adapter
        
        viewModel.reservations.observe(viewLifecycleOwner) {
            updateTabList(adapter, binding.tabLayout.selectedTabPosition)
        }
        
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                updateTabList(adapter, tab?.position ?: 0)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        
        viewModel.loadReservations("user001")
    }
    
    private fun updateTabList(adapter: ReservationAdapter, position: Int) {
        val status = when (position) {
            1 -> ReservationStatus.COMPLETED
            2 -> ReservationStatus.CANCELLED
            else -> ReservationStatus.UPCOMING
        }
        val list = viewModel.reservations.value.orEmpty().filter { it.status == status }
        adapter.submitList(list)
        binding.tvEmptyState.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
