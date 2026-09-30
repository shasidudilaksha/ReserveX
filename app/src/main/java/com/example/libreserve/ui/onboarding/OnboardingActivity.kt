package com.example.libreserve.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.libreserve.R
import com.example.libreserve.databinding.ActivityOnboardingBinding
import com.example.libreserve.ui.auth.AuthActivity
import com.example.libreserve.utils.SessionManager

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var sessionManager: SessionManager
    private val onboardingPages = listOf(
        OnboardingPage("Find Books", "Easily search and find books available in the library.", R.drawable.ic_book),
        OnboardingPage("Reserve Seat", "Book your preferred study space in advance.", R.drawable.ic_seat),
        OnboardingPage("Book Rooms", "Reserve meeting rooms for group study sessions.", R.drawable.ic_meeting_room)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        val adapter = OnboardingAdapter(onboardingPages)
        binding.viewPager.adapter = adapter

        setupDots()
        updateDots(0)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateDots(position)
                if (position == onboardingPages.size - 1) {
                    binding.btnNext.text = "Get Started"
                    binding.btnSkip.visibility = View.INVISIBLE
                } else {
                    binding.btnNext.text = "Next"
                    binding.btnSkip.visibility = View.VISIBLE
                }
            }
        })

        binding.btnNext.setOnClickListener {
            if (binding.viewPager.currentItem + 1 < onboardingPages.size) {
                binding.viewPager.currentItem += 1
            } else {
                finishOnboarding()
            }
        }

        binding.btnSkip.setOnClickListener {
            finishOnboarding()
        }
    }

    private fun setupDots() {
        val dots = arrayOfNulls<ImageView>(onboardingPages.size)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(8, 0, 8, 0)
        }
        for (i in dots.indices) {
            dots[i] = ImageView(this).apply {
                setImageDrawable(ContextCompat.getDrawable(this@OnboardingActivity, R.drawable.bg_dot_inactive))
                this.layoutParams = layoutParams
            }
            binding.layoutDots.addView(dots[i])
        }
    }

    private fun updateDots(position: Int) {
        for (i in 0 until binding.layoutDots.childCount) {
            val dot = binding.layoutDots.getChildAt(i) as ImageView
            if (i == position) {
                dot.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bg_dot_active
))
            } else {
                dot.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bg_dot_inactive))
            }
        }
    }

    private fun finishOnboarding() {
        sessionManager.isFirstLaunch = false
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }
}
