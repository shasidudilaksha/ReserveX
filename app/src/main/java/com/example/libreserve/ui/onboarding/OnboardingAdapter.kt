package com.example.libreserve.ui.onboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.libreserve.databinding.ItemOnboardingPageBinding

data class OnboardingPage(val title: String, val description: String, val iconRes: Int)

class OnboardingAdapter(private val pages: List<OnboardingPage>) : RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    inner class OnboardingViewHolder(private val binding: ItemOnboardingPageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(page: OnboardingPage) {
            binding.tvTitle.text = page.title
            binding.tvDescription.text = page.description
            binding.ivIcon.setImageResource(page.iconRes)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val binding = ItemOnboardingPageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OnboardingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(pages[position])
    }

    override fun getItemCount() = pages.size
}
