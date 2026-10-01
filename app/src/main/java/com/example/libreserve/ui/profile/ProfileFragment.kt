package com.example.libreserve.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.libreserve.databinding.FragmentProfileBinding
import com.example.libreserve.ui.auth.AuthActivity
import com.example.libreserve.utils.SessionManager
import com.example.libreserve.viewmodel.AuthResult
import com.example.libreserve.viewmodel.AuthViewModel

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager
    private val viewModel: AuthViewModel by viewModels()

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
        
        val currentName = sessionManager.userName
        val currentEmail = sessionManager.userEmail
        
        binding.tvHeaderName.text = currentName
        binding.tvHeaderEmail.text = currentEmail
        
        binding.etName.setText(currentName)
        
        binding.btnSave.setOnClickListener {
            val newName = binding.etName.text.toString().trim()
            val newPassword = binding.etPassword.text.toString()
            viewModel.saveProfile(newName, newPassword)
        }
        
        binding.btnLogout.setOnClickListener {
            viewModel.logout()
        }

        viewModel.busy.observe(viewLifecycleOwner) { busy ->
            binding.btnSave.isEnabled = !busy
            binding.btnLogout.isEnabled = !busy
            binding.etName.isEnabled = !busy
            binding.etPassword.isEnabled = !busy
        }
        viewModel.result.observe(viewLifecycleOwner) { result ->
            when (result) {
                is AuthResult.Profile -> {
                    viewModel.consumeResult()
                    binding.tvHeaderName.text = result.user.fullName
                    binding.tvHeaderEmail.text = result.user.email
                    binding.etName.setText(result.user.fullName)
                    if (result.saved) {
                        binding.etPassword.text?.clear()
                        Toast.makeText(requireContext(), "Profile saved", Toast.LENGTH_SHORT).show()
                    }
                }
                is AuthResult.LoggedOut -> {
                    viewModel.consumeResult()
                    result.warning?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show() }
                    startActivity(Intent(requireContext(), AuthActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                }
                is AuthResult.Failure -> {
                    viewModel.consumeResult()
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG).show()
                }
                else -> Unit
            }
        }
        if (savedInstanceState == null) viewModel.loadProfile()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
