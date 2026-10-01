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
import com.example.libreserve.viewmodel.AuthResult

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

            viewModel.register(name, studentId, email, password,
                binding.tilConfirmPassword.editText?.text.toString(), binding.cbTerms.isChecked)
        }

        viewModel.busy.observe(viewLifecycleOwner) { busy ->
            binding.btnSignUp.isEnabled = !busy
            binding.tvLogin.isEnabled = !busy
            binding.btnSignUp.text = if (busy) "Creating account…" else "Create Account"
        }
        viewModel.result.observe(viewLifecycleOwner) { result ->
            when (result) {
                is AuthResult.Registered -> {
                    viewModel.consumeResult()
                    val message = if (result.confirmationRequired)
                        "Check your email to confirm registration, then sign in. If you already have an account, use sign in."
                    else "Account created. You can now sign in."
                    binding.tilPassword.editText?.text?.clear()
                    binding.tilConfirmPassword.editText?.text?.clear()
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                    findNavController().navigateUp()
                }
                is AuthResult.Failure -> {
                    viewModel.consumeResult()
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG).show()
                }
                else -> Unit
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
