package com.example.libreserve

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.libreserve.databinding.ActivitySplashBinding
import com.example.libreserve.ui.auth.AuthActivity
import com.example.libreserve.ui.onboarding.OnboardingActivity
import com.example.libreserve.utils.SessionManager

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        sessionManager = SessionManager(this)

        binding.ivLogo.animate().scaleX(1.2f).scaleY(1.2f).alpha(1f).setDuration(1500).start()
        binding.tvAppName.animate().alpha(1f).setDuration(1500).start()
        binding.tvSlogan.animate().alpha(1f).setDuration(1500).start()

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = when {
                sessionManager.isFirstLaunch -> Intent(this, OnboardingActivity::class.java)
                sessionManager.isLoggedIn -> Intent(this, MainActivity::class.java)
                else -> Intent(this, AuthActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, 2000)
    }
}
