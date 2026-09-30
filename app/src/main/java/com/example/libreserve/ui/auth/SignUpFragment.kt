package com.example.libreserve.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.libreserve.databinding.FragmentSignUpBinding
import com.example.libreserve.viewmodel.AuthViewModel

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignUp.setOnClickListener {
            val name = binding.tilName.editText?.text.toString()
            val studentId = binding.tilStudentId.editText?.text.toString()
            val email = binding.tilEmail.editText?.text.toString()
            val password = binding.tilPassword.editText?.text.toString()

            if (name.isNotEmpty() && studentId.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                val sessionManager = com.example.libreserve.utils.SessionManager(requireContext())
                sessionManager.registeredName = name
                sessionManager.registeredStudentId = studentId
                sessionManager.registeredEmail = email
                sessionManager.registeredPassword = password
                Toast.makeText(requireContext(), "Account created successfully", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } else {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.tvLogin.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
