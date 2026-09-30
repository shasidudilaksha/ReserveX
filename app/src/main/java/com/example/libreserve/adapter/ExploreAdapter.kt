package com.example.libreserve.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.libreserve.R
import com.example.libreserve.databinding.ItemExploreResultBinding
import com.example.libreserve.model.Book
import com.example.libreserve.model.MeetingRoom
import com.example.libreserve.model.ReadingArea

sealed class ExploreItem {
    data class BookItem(val book: Book) : ExploreItem()
    data class AreaItem(val area: ReadingArea) : ExploreItem()
    data class RoomItem(val room: MeetingRoom) : ExploreItem()
}

class ExploreAdapter(private val onItemClick: (ExploreItem) -> Unit) :
    ListAdapter<ExploreItem, ExploreAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(private val binding: ItemExploreResultBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ExploreItem) {
            when (item) {
                is ExploreItem.BookItem -> {
                    binding.tvTitle.text = item.book.title
                    binding.tvSubtitle.text = item.book.author
                    binding.ivIcon.setImageResource(R.drawable.ic_book)
                    binding.tvBadge.text = if (item.book.isAvailable) "Available" else "Reserved"
                }
                is ExploreItem.AreaItem -> {
                    binding.tvTitle.text = item.area.name
                    binding.tvSubtitle.text = "Reading Area - ${item.area.totalSeats} seats"
                    binding.ivIcon.setImageResource(R.drawable.ic_seat)
                    binding.tvBadge.text = "Open"
                }
                is ExploreItem.RoomItem -> {
                    binding.tvTitle.text = item.room.name
                    binding.tvSubtitle.text = "Capacity: ${item.room.capacity} persons"
                    binding.ivIcon.setImageResource(R.drawable.ic_meeting_room)
                    binding.tvBadge.text = if (item.room.isAvailable) "Available" else "Busy"
                }
            }
            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemExploreResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ExploreItem>() {
        override fun areItemsTheSame(oldItem: ExploreItem, newItem: ExploreItem): Boolean {
            return when {
                oldItem is ExploreItem.BookItem && newItem is ExploreItem.BookItem -> oldItem.book.bookId == newItem.book.bookId
                oldItem is ExploreItem.AreaItem && newItem is ExploreItem.AreaItem -> oldItem.area.areaId == newItem.area.areaId
                oldItem is ExploreItem.RoomItem && newItem is ExploreItem.RoomItem -> oldItem.room.roomId == newItem.room.roomId
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: ExploreItem, newItem: ExploreItem): Boolean {
            return oldItem == newItem
        }
    }
}
