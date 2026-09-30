package com.example.libreserve.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.libreserve.R
import com.example.libreserve.databinding.ItemTimeSlotBinding
import com.example.libreserve.model.SlotStatus
import com.example.libreserve.model.TimeSlot
import kotlin.math.roundToInt

class TimeSlotAdapter(private val onSlotClick: (TimeSlot) -> Unit) :
    ListAdapter<TimeSlot, TimeSlotAdapter.SlotViewHolder>(SlotDiffCallback()) {

    private var selectedSlotId: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
        val binding = ItemTimeSlotBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SlotViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSelectedSlot(slotId: String?) {
        if (selectedSlotId == slotId) return
        selectedSlotId = slotId
        notifyDataSetChanged()
    }

    inner class SlotViewHolder(private val binding: ItemTimeSlotBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(slot: TimeSlot) {
            val context = binding.root.context
            val isSelected = slot.slotId == selectedSlotId
            val isAvailable = slot.status == SlotStatus.AVAILABLE

            binding.textViewTime.text = "${slot.startTime} - ${slot.endTime}"
            binding.textViewSlotMeta.text = if (isAvailable) {
                "Room access and display setup included"
            } else {
                "Already reserved for this time"
            }

            when {
                !isAvailable -> {
                    binding.root.alpha = 0.72f
                    binding.root.isEnabled = false
                    binding.root.isClickable = false
                    binding.root.setOnClickListener(null)
                    binding.root.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
                    binding.root.setStrokeColor(ContextCompat.getColor(context, R.color.colorDivider))
                    binding.root.strokeWidth = dp(1)
                    binding.iconContainer.backgroundTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorUnavailableLight))
                    binding.iconSlot.setColorFilter(ContextCompat.getColor(context, R.color.colorUnavailable))
                    binding.textViewStatus.text = "Booked"
                    binding.textViewStatus.setTextColor(ContextCompat.getColor(context, R.color.colorReserved))
                    binding.textViewStatus.setBackgroundResource(R.drawable.bg_status_reserved)
                }

                isSelected -> {
                    binding.root.alpha = 1f
                    binding.root.isEnabled = true
                    binding.root.isClickable = true
                    binding.root.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorSelectedLight))
                    binding.root.setStrokeColor(ContextCompat.getColor(context, R.color.colorSelected))
                    binding.root.strokeWidth = dp(2)
                    binding.iconContainer.backgroundTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorLightOrange))
                    binding.iconSlot.setColorFilter(ContextCompat.getColor(context, R.color.colorSelected))
                    binding.textViewStatus.text = "Selected"
                    binding.textViewStatus.setTextColor(ContextCompat.getColor(context, R.color.colorWarning))
                    binding.textViewStatus.setBackgroundResource(R.drawable.bg_time_slot_selected)
                    binding.root.setOnClickListener { onSlotClick(slot) }
                }

                else -> {
                    binding.root.alpha = 1f
                    binding.root.isEnabled = true
                    binding.root.isClickable = true
                    binding.root.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
                    binding.root.setStrokeColor(ContextCompat.getColor(context, R.color.colorDivider))
                    binding.root.strokeWidth = dp(1)
                    binding.iconContainer.backgroundTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorAvailableLight))
                    binding.iconSlot.setColorFilter(ContextCompat.getColor(context, R.color.colorRoomsFeature))
                    binding.textViewStatus.text = "Available"
                    binding.textViewStatus.setTextColor(ContextCompat.getColor(context, R.color.colorSuccess))
                    binding.textViewStatus.setBackgroundResource(R.drawable.bg_status_available)
                    binding.root.setOnClickListener { onSlotClick(slot) }
                }
            }
        }

        private fun dp(value: Int): Int {
            return (value * binding.root.resources.displayMetrics.density).roundToInt()
        }
    }
}

class SlotDiffCallback : DiffUtil.ItemCallback<TimeSlot>() {
    override fun areItemsTheSame(oldItem: TimeSlot, newItem: TimeSlot): Boolean {
        return oldItem.slotId == newItem.slotId
    }

    override fun areContentsTheSame(oldItem: TimeSlot, newItem: TimeSlot): Boolean {
        return oldItem == newItem
    }
}
