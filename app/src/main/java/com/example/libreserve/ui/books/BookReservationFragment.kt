package com.example.libreserve.ui.books

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.libreserve.repository.BookRepository
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.libreserve.R
import com.example.libreserve.databinding.FragmentBookReservationBinding
import com.example.libreserve.model.Reservation
import com.example.libreserve.model.ReservationStatus
import com.example.libreserve.model.ReservationType
import com.example.libreserve.viewmodel.ReservationViewModel
import java.text.SimpleDateFormat
import java.util.*

class BookReservationFragment : Fragment() {

    private var _binding: FragmentBookReservationBinding? = null
    private val binding get() = _binding!!
    private val reservationViewModel: ReservationViewModel by activityViewModels()
    
    private var selectedCalendar = Calendar.getInstance()
    private var dateSelected = false
    private var timeSelected = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookReservationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bookId = arguments?.getString("bookId") ?: ""
        val book = BookRepository().getBookById(bookId)
        val bookTitle = book?.title ?: arguments?.getString("bookTitle").orEmpty()
        val shelf = book?.shelfLocation.orEmpty()
        binding.tvShelf.text = "Shelf $shelf"
        savedInstanceState?.let {
            selectedCalendar.timeInMillis = it.getLong("pickupMillis", selectedCalendar.timeInMillis)
            dateSelected = it.getBoolean("dateSelected")
            timeSelected = it.getBoolean("timeSelected")
        }
        updateDateTimeDisplay()
        
        binding.textViewConfirmMessage.text = "Reserve: $bookTitle"
        
        binding.btnSelectDate.setOnClickListener {
            val calendar = selectedCalendar
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    selectedCalendar.set(Calendar.YEAR, year)
                    selectedCalendar.set(Calendar.MONTH, month)
                    selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    dateSelected = true
                    updateDateTimeDisplay()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).apply { datePicker.minDate = System.currentTimeMillis() }.show()
        }
        
        binding.btnSelectTime.setOnClickListener {
            val calendar = selectedCalendar
            TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    selectedCalendar.set(Calendar.MINUTE, minute)
                    timeSelected = true
                    updateDateTimeDisplay()
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            ).show()
        }
        
        binding.buttonConfirm.setOnClickListener {
            if (!dateSelected || !timeSelected || selectedCalendar.timeInMillis <= System.currentTimeMillis()) {
                Toast.makeText(requireContext(), "Choose a future pick-up date and time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (book == null || !book.isAvailable) {
                Toast.makeText(requireContext(), "This book is currently unavailable", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            binding.buttonConfirm.isEnabled = false
            val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            
            val dateStr = dateFormat.format(selectedCalendar.time)
            val timeStr = timeFormat.format(selectedCalendar.time)
            
            val reservation = Reservation(
                reservationId = UUID.randomUUID().toString(),
                userId = "user001",
                type = ReservationType.BOOK,
                resourceId = bookId,
                resourceName = bookTitle,
                libraryId = "lib001",
                libraryName = "SLIIT Malabe Library",
                date = dateStr,
                startTime = timeStr,
                endTime = timeStr,
                status = ReservationStatus.UPCOMING,
                location = "Shelf $shelf"
            )
            reservationViewModel.addReservation(reservation, "user001")
            
            val bundle = Bundle().apply {
                putString("reservationType", ReservationType.BOOK.name)
                putString("resourceName", bookTitle)
                putString("dateStr", dateStr)
                putString("startTime", timeStr)
                putString("endTime", timeStr)
                putString("libraryName", "SLIIT Malabe Library")
                putString("locationStr", "Shelf $shelf")
            }
            findNavController().navigate(R.id.action_book_reservation_to_confirmation, bundle)
        }
    }
    
    private fun updateDateTimeDisplay() {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        
        val datePart = if (dateSelected) dateFormat.format(selectedCalendar.time) else "Not selected"
        val timePart = if (timeSelected) timeFormat.format(selectedCalendar.time) else "Not selected"
        
        binding.tvSelectedDateTime.text = "Date: $datePart\nTime: $timePart"
        binding.btnSelectDate.text = if (dateSelected) datePart else "Select pick-up date"
        binding.btnSelectTime.text = if (timeSelected) timePart else "Select pick-up time"
        binding.buttonConfirm.isEnabled = dateSelected && timeSelected
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("pickupMillis", selectedCalendar.timeInMillis)
        outState.putBoolean("dateSelected", dateSelected)
        outState.putBoolean("timeSelected", timeSelected)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
