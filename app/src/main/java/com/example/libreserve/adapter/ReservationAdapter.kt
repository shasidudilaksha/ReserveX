package com.example.libreserve.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.libreserve.R
import com.example.libreserve.databinding.ItemReservationBinding
import com.example.libreserve.model.Reservation
import com.example.libreserve.model.ReservationStatus
import com.example.libreserve.model.ReservationType

class ReservationAdapter(
    private val onCancelClick: (Reservation) -> Unit,
    private val onModifyClick: (Reservation) -> Unit
) : ListAdapter<Reservation, ReservationAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(private val binding: ItemReservationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(reservation: Reservation) {
            binding.tvResourceName.text = reservation.resourceName
            binding.tvDate.text = reservation.date
            binding.tvTime.text = if (reservation.type == ReservationType.BOOK) {
                "Pick-up: ${reservation.startTime}"
            } else {
                "${reservation.startTime} - ${reservation.endTime}"
            }

            val iconRes = when (reservation.type) {
                ReservationType.BOOK -> R.drawable.ic_book
                ReservationType.SEAT -> R.drawable.ic_seat
                ReservationType.MEETING_ROOM -> R.drawable.ic_meeting_room
            }
            binding.ivTypeIcon.setImageResource(iconRes)

            if (reservation.status == ReservationStatus.UPCOMING) {
                binding.actionsRow.visibility = View.VISIBLE
                binding.btnCancel.setOnClickListener { onCancelClick(reservation) }
                binding.btnModify.setOnClickListener { onModifyClick(reservation) }
            } else {
                binding.actionsRow.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemReservationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Reservation>() {
        override fun areItemsTheSame(oldItem: Reservation, newItem: Reservation): Boolean {
            return oldItem.reservationId == newItem.reservationId
        }

        override fun areContentsTheSame(oldItem: Reservation, newItem: Reservation): Boolean {
            return oldItem == newItem
        }
    }
}
