package com.example.metask.view.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.metask.R
import com.example.metask.databinding.FragmentLoginFragmentBinding
import com.example.metask.utils.FragmentCommunicator
import com.example.metask.view.home.MainActivity
import com.example.metask.viewModel.LoginViewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginFragmentBinding? = null
    private val viewModel by viewModels<LoginViewModel>()
    var isValid: Boolean = false
    private lateinit var communicator: FragmentCommunicator
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        communicator = requireActivity() as MainActivity
        _binding = FragmentLoginFragmentBinding.inflate(inflater, container, false)
        setupObservers()
        setupView()
        return binding.root
    }

    private fun setupView() {

        binding.register.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.btnIngresar.setOnClickListener {
            if (validateInputs()) {
                requestLogin()
            } else {
                Toast.makeText(activity, "Inicio de sesión invalido", Toast.LENGTH_SHORT).show()
            }
        }

        binding.emailTiet.addTextChangedListener {

            if (binding.emailTiet.text.toString().isEmpty()) {
                binding.emailTil.error = "Introduce un correo"
                isValid = false
            } else {
                isValid = true
            }
        }

        binding.passwordTiet.addTextChangedListener {

            if (binding.passwordTiet.text.toString().isEmpty()) {
                binding.passwordTil.error = "Introduce tu contraseña"
                isValid = false
            } else {
                isValid = true
            }
        }

        // Configuración del ícono para mostrar u ocultar contraseña
        binding.passwordTil.setEndIconOnClickListener {
            togglePasswordVisibility()
        }

        setupObservers()
    }

    /**
     * Cambia la visibilidad de la contraseña entre visible y oculta.
     */
    private fun togglePasswordVisibility() {
        val editText = binding.passwordTiet
        if (editText.inputType == (android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            // Mostrar la contraseña
            editText.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            // Ocultar la contraseña
            editText.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        // Mueve el cursor al final del texto después de cambiar el tipo de entrada
        editText.setSelection(editText.text?.length ?: 0)
    }

    private fun validateInputs(): Boolean {
        val emailNotEmpty = binding.emailTiet.text.toString().isNotEmpty()
        val passwordNotEmpty = binding.passwordTiet.text.toString().isNotEmpty()

        isValid = emailNotEmpty && passwordNotEmpty

        binding.emailTil.error = if (!emailNotEmpty) "Introduce un correo" else null
        binding.passwordTil.error = if (!passwordNotEmpty) "Introduce tu contraseña" else null

        return isValid
    }

    private fun setupObservers() {
        viewModel.loaderState.observe(viewLifecycleOwner) { loaderState ->
            communicator.showLoader(loaderState)
        }
        viewModel.sessionValid.observe(viewLifecycleOwner) { validSession ->
            if (validSession) {
                findNavController().navigate(R.id.action_loginFragment_to_pendingTaskFragment)
            } else {
                Toast.makeText(activity, "Ingreso invalido", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestLogin() {
        viewModel.requestSignIn(binding.emailTiet.text.toString(),
            binding.passwordTiet.text.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
