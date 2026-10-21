package com.example.libreserve.ui.books

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.libreserve.R
import com.example.libreserve.databinding.FragmentBookReservationBinding
import com.example.libreserve.model.Reservation
import com.example.libreserve.model.ReservationStatus
import com.example.libreserve.model.ReservationType
import com.example.libreserve.viewmodel.ReservationViewModel
import com.example.libreserve.viewmodel.BookViewModel
import java.text.SimpleDateFormat
import java.util.*

class BookReservationFragment : Fragment() {

    private var _binding: FragmentBookReservationBinding? = null
    private val binding get() = _binding!!
    private val reservationViewModel: ReservationViewModel by activityViewModels()
    private val bookViewModel: BookViewModel by activityViewModels()
    
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
        var book = bookViewModel.selectedBook.value
        val bookTitle = arguments?.getString("bookTitle").orEmpty()
        var shelf = ""
        bookViewModel.selectedBook.observe(viewLifecycleOwner) {
            book = it
            shelf = it?.shelfLocation.orEmpty()
            binding.tvShelf.text = "Shelf $shelf"
            binding.textViewConfirmMessage.text = "Reserve: ${it?.title ?: bookTitle}"
        }
        bookViewModel.loadBook(bookId)
        savedInstanceState?.let {
            selectedCalendar.timeInMillis = it.getLong("pickupMillis", selectedCalendar.timeInMillis)
            dateSelected = it.getBoolean("dateSelected")
            timeSelected = it.getBoolean("timeSelected")
        }
        updateDateTimeDisplay()
        
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
            binding.buttonConfirm.isEnabled = false
            val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            
            val dateStr = dateFormat.format(selectedCalendar.time)
            val timeStr = timeFormat.format(selectedCalendar.time)
            
            val currentBook = book
            if (currentBook == null) {
                binding.buttonConfirm.isEnabled = true
                Toast.makeText(requireContext(), "Book details could not be loaded", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val reservation = Reservation(
                reservationId = UUID.randomUUID().toString(),
                userId = com.example.libreserve.utils.SessionManager(requireContext()).userId,
                type = ReservationType.BOOK,
                resourceId = bookId,
                resourceName = currentBook.title,
                libraryId = "lib001",
                libraryName = "SLIIT Malabe Library",
                date = dateStr,
                startTime = timeStr,
                endTime = timeStr,
                status = ReservationStatus.UPCOMING,
                location = "Shelf ${currentBook.shelfLocation}"
            )
            val userId = com.example.libreserve.utils.SessionManager(requireContext()).userId
            reservationViewModel.addBookReservation(reservation, userId) { result ->
                if (!isAdded) return@addBookReservation
                if (result.isFailure) {
                    binding.buttonConfirm.isEnabled = true
                    Toast.makeText(requireContext(), result.exceptionOrNull()?.message ?: "Unable to reserve book", Toast.LENGTH_SHORT).show()
                    return@addBookReservation
                }
                val bundle = Bundle().apply {
                    putString("reservationType", ReservationType.BOOK.name)
                    putString("resourceName", currentBook.title)
                    putString("dateStr", dateStr)
                    putString("startTime", timeStr)
                    putString("endTime", timeStr)
                    putString("libraryName", "SLIIT Malabe Library")
                    putString("locationStr", "Shelf ${currentBook.shelfLocation}")
                }
                findNavController().navigate(R.id.action_book_reservation_to_confirmation, bundle)
            }
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
