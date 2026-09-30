package com.example.libreserve.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.libreserve.R
import com.example.libreserve.databinding.ItemSeatBinding
import com.example.libreserve.model.Seat
import com.example.libreserve.model.SeatStatus

class SeatAdapter(private val onSeatClick: (Seat) -> Unit) :
    ListAdapter<Seat, SeatAdapter.SeatViewHolder>(SeatDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeatViewHolder {
        val binding = ItemSeatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SeatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SeatViewHolder, position: Int) {
        val seat = getItem(position)
        holder.bind(seat)
    }

    override fun onViewRecycled(holder: SeatViewHolder) {
        holder.itemView.animate().cancel()
        holder.itemView.scaleX = 1f
        holder.itemView.scaleY = 1f
        holder.itemView.isSelected = false
        super.onViewRecycled(holder)
    }

    inner class SeatViewHolder(private val binding: ItemSeatBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(seat: Seat) {
            binding.textViewSeatNumber.text = seat.seatNumber
            
            val selected = seat.status == SeatStatus.SELECTED
            val available = seat.status == SeatStatus.AVAILABLE
            val (background, foreground) = when (seat.status) {
                SeatStatus.AVAILABLE -> "#E6F2EA" to "#27755A"
                SeatStatus.SELECTED -> "#7051AA" to "#FFFFFF"
                SeatStatus.RESERVED -> "#F4E6EA" to "#9D6370"
                SeatStatus.UNAVAILABLE -> "#EFEDF1" to "#8C8595"
            }
            val label = when (seat.status) {
                SeatStatus.AVAILABLE -> "Free"
                SeatStatus.SELECTED -> "Selected"
                SeatStatus.RESERVED -> "Booked"
                SeatStatus.UNAVAILABLE -> "Closed"
            }
            val wasSelected = binding.root.isSelected
            binding.root.setCardBackgroundColor(Color.parseColor(background))
            binding.root.strokeColor = Color.parseColor(if (selected) "#7051AA" else background)
            binding.root.isSelected = selected
            binding.root.isEnabled = available || selected
            binding.root.contentDescription = "Seat " + seat.seatNumber + ", " + label
            binding.textViewSeatNumber.setTextColor(Color.parseColor(foreground))
            binding.ivSeat.setColorFilter(Color.parseColor(foreground))
            binding.tvSeatStatus.text = label
            binding.tvSeatStatus.setTextColor(Color.parseColor(foreground))
            binding.root.animate().cancel()
            binding.root.scaleX = 1f
            binding.root.scaleY = 1f
            if (selected && !wasSelected) com.example.libreserve.utils.UiMotion.select(binding.root)
            binding.root.setOnClickListener { onSeatClick(seat) }
        }
    }
}

class SeatDiffCallback : DiffUtil.ItemCallback<Seat>() {
    override fun areItemsTheSame(oldItem: Seat, newItem: Seat): Boolean {
        return oldItem.seatId == newItem.seatId
    }

    override fun areContentsTheSame(oldItem: Seat, newItem: Seat): Boolean {
        return oldItem == newItem
    }
}
