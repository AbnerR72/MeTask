package com.example.metask.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.metask.R
import com.example.metask.databinding.FragmentRegisterFragmentBinding
import com.example.metask.utils.FragmentCommunicator

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<RegisterViewModel>()
    var isValid: Boolean = false
    private lateinit var communicator: FragmentCommunicator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        communicator = requireActivity() as MainActivity
        _binding = FragmentRegisterFragmentBinding.inflate(inflater, container, false)
        setupView()
        return binding.root
    }

    private fun setupView() {
        binding.imageButton3.setOnClickListener {
            viewModel.requestSignUp(binding.emailTiet.text.toString(),
                binding.passwordTiet.text.toString())
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.btnRegistrar.setOnClickListener {
            val email = binding.emailTiet.text.toString().trim()
            val password = binding.passwordTiet.text.toString().trim()
            val name = binding.nameTiet.text.toString().trim()

            if (name.isEmpty()) {
                binding.nameTiet.error = "El nombre es obligatorio"
                return@setOnClickListener
            }

            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailTil.error = "Introduce un correo válido"
                return@setOnClickListener
            }

            if (password.isEmpty() || password.length < 6) {
                binding.passwordTil.error = "Introduce una contraseña de al menos 6 caracteres"
                return@setOnClickListener
            }

            viewModel.requestSignUp(email, password)
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)

        }

        setupObservers()
    }

    private fun setupObservers() {
        viewModel.loaderState.observe(viewLifecycleOwner) { loaderState ->
            communicator.showLoader(loaderState)
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}