package com.example.libreserve.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.libreserve.databinding.FragmentProfileBinding
import com.example.libreserve.ui.auth.AuthActivity
import com.example.libreserve.utils.SessionManager

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())
        
        val currentName = sessionManager.userName.ifEmpty { "Alex Student" }
        val currentEmail = sessionManager.userEmail.ifEmpty { "alex@university.edu" }
        
        binding.tvHeaderName.text = currentName
        binding.tvHeaderEmail.text = currentEmail
        
        binding.etName.setText(currentName)
        
        binding.btnSave.setOnClickListener {
            val newName = binding.etName.text.toString().trim()
            val newPassword = binding.etPassword.text.toString().trim()
            
            var isUpdated = false
            
            if (newName.isNotEmpty() && newName != sessionManager.userName) {
                sessionManager.userName = newName
                sessionManager.registeredName = newName
                binding.tvHeaderName.text = newName
                isUpdated = true
            }
            
            if (newPassword.isNotEmpty()) {
                sessionManager.registeredPassword = newPassword
                binding.etPassword.text?.clear()
                isUpdated = true
            }
            
            if (isUpdated) {
                Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.btnLogout.setOnClickListener {
            sessionManager.logout()
            val intent = Intent(requireContext(), AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
