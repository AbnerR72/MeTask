package com.example.metask.view.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.metask.R
import com.example.metask.databinding.FragmentRegisterFragmentBinding
import com.example.metask.utils.FragmentCommunicator
import com.example.metask.view.home.MainActivity
import com.example.metask.viewModel.RegisterViewModel


class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<RegisterViewModel>()
    private lateinit var communicator: FragmentCommunicator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        communicator = requireActivity() as MainActivity
        _binding = FragmentRegisterFragmentBinding.inflate(inflater, container, false)
        setupView()
        setupObservers()
        return binding.root
    }

    private fun setupView() {

        binding.flecha.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnRegistrar.setOnClickListener {
            val email = binding.emailTiet.text.toString().trim()
            val password = binding.passwordTiet.text.toString().trim()
            val name = binding.nameTiet.text.toString().trim()

            // Reset errors
            binding.nameTiet.error = null
            binding.emailTil.error = null
            binding.passwordTil.error = null

            // Validaciones
            if (name.isEmpty()) {
                binding.nameTiet.error = "El nombre es obligatorio"
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                binding.emailTil.error = "El correo es obligatorio"
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailTil.error = "Introduce un correo válido"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.passwordTil.error = "La contraseña es obligatoria"
                return@setOnClickListener
            }

            if (password.length < 6) {
                binding.passwordTil.error = "Mínimo 6 caracteres"
                return@setOnClickListener
            }

            viewModel.requestSignUp(email, password)
        }
    }

    private fun setupObservers() {
        viewModel.loaderState.observe(viewLifecycleOwner) { isLoading ->
            communicator.showLoader(isLoading)
        }

        viewModel.errorState.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }

        // Observador para éxito en registro
        viewModel.signUpSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(
                    requireContext(),
                    "Registro exitoso",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}