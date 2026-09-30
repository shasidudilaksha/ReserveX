package com.example.libreserve.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.libreserve.R
import com.example.libreserve.databinding.ItemMeetingRoomBinding
import com.example.libreserve.model.MeetingRoom

class MeetingRoomAdapter(private val onRoomClick: (MeetingRoom) -> Unit) :
    ListAdapter<MeetingRoom, MeetingRoomAdapter.RoomViewHolder>(RoomDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val binding = ItemMeetingRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RoomViewHolder(private val binding: ItemMeetingRoomBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(room: MeetingRoom) {
            val context = binding.root.context

            binding.textViewRoomName.text = room.name
            binding.textViewCapacity.text = "${room.capacity} people"
            binding.textViewLocation.text = "Floor ${room.floor}"
            binding.textViewFacilities.text = room.equipment.joinToString(" - ")

            if (room.isAvailable) {
                binding.root.alpha = 1f
                binding.root.isEnabled = true
                binding.textViewAvailability.text = "Open"
                binding.textViewAvailability.setTextColor(ContextCompat.getColor(context, R.color.colorSuccess))
                binding.textViewAvailability.setBackgroundResource(R.drawable.bg_status_available)
                binding.root.setOnClickListener { onRoomClick(room) }
            } else {
                binding.root.alpha = 0.65f
                binding.root.isEnabled = false
                binding.textViewAvailability.text = "Closed"
                binding.textViewAvailability.setTextColor(ContextCompat.getColor(context, R.color.colorReserved))
                binding.textViewAvailability.setBackgroundResource(R.drawable.bg_status_reserved)
                binding.root.setOnClickListener(null)
            }
        }
    }
}

class RoomDiffCallback : DiffUtil.ItemCallback<MeetingRoom>() {
    override fun areItemsTheSame(oldItem: MeetingRoom, newItem: MeetingRoom): Boolean {
        return oldItem.roomId == newItem.roomId
    }

    override fun areContentsTheSame(oldItem: MeetingRoom, newItem: MeetingRoom): Boolean {
        return oldItem == newItem
    }
}
