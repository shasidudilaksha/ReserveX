package com.example.libreserve.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.libreserve.MainActivity
import com.example.libreserve.R
import com.example.libreserve.databinding.FragmentLoginBinding
import com.example.libreserve.viewmodel.AuthViewModel
import com.example.libreserve.viewmodel.AuthResult

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            val email = binding.tilEmail.editText?.text.toString()
            val password = binding.tilPassword.editText?.text.toString()

            viewModel.login(email, password)
        }

        viewModel.busy.observe(viewLifecycleOwner) { busy ->
            binding.btnLogin.isEnabled = !busy
            binding.tvSignUp.isEnabled = !busy
            binding.btnLogin.text = if (busy) "Signing in…" else "Sign In"
        }
        viewModel.result.observe(viewLifecycleOwner) { result ->
            when (result) {
                AuthResult.LoggedIn -> {
                    viewModel.consumeResult()
                    binding.tilPassword.editText?.text?.clear()
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                    requireActivity().finish()
                }
                is AuthResult.Failure -> {
                    viewModel.consumeResult()
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG).show()
                }
                else -> Unit
            }
        }

        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_signup)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
