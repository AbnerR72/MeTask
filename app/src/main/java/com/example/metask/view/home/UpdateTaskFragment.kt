package com.example.metask.view.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.metask.R

import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

import android.app.DatePickerDialog

import com.example.metask.databinding.FragmentUpdateTaskBinding
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.fragment.findNavController
import java.util.*


class UpdateTaskFragment : Fragment() {
    private var _binding: FragmentUpdateTaskBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private var taskId: String = ""
    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()
        taskId = arguments?.getString("taskId") ?: ""

        if (taskId.isEmpty()) {
            requireActivity().onBackPressed()
            return
        }

        loadTaskData()
        setupClickListeners()
    }

    private fun loadTaskData() {
        db.collection("tasks").document(taskId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    binding.nombreTIK.setText(document.getString("name"))
                    binding.descriptionTIK.setText(document.getString("description"))

                    val date = document.getDate("bornDate") ?: Date()
                    calendar.time = date

                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    binding.FechaTIK.setText(dateFormat.format(date))
                }
            }
    }

    private fun setupClickListeners() {
        // Configurar el ícono de flecha
        binding.iconFlecha2.setOnClickListener {
            navigateBackToPendingTasks()
        }

        // Configurar el campo de fecha
        binding.FechaTIK.setOnClickListener {
            showDatePicker()
        }

        // Configurar el botón de actualizar
        binding.buttonSecond.setOnClickListener {
            updateTask()
        }
    }

    private fun navigateBackToPendingTasks() {
        try {
            findNavController().navigate(R.id.action_updateTaskFragment_to_pendingTaskFragment)
        } catch (e: Exception) {
            // Fallback si hay algún error con Navigation Component
            requireActivity().onBackPressed()
        }
    }

    private fun showDatePicker() {
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                calendar.set(year, month, day)
                updateDateInView()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateDateInView() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.FechaTIK.setText(dateFormat.format(calendar.time))
    }

    private fun updateTask() {
        val name = binding.nombreTIK.text.toString().trim()
        val description = binding.descriptionTIK.text.toString().trim()
        val date = calendar.time

        if (name.isEmpty() || description.isEmpty()) {
            // Mostrar error (puedes usar Snackbar)
            Snackbar.make(binding.root, "Nombre y descripción son requeridos", Snackbar.LENGTH_SHORT).show()
            return
        }

        val updates = hashMapOf<String, Any>(
            "name" to name,
            "description" to description,
            "bornDate" to date
        )

        db.collection("tasks").document(taskId)
            .update(updates)
            .addOnSuccessListener {
                // Navegar de regreso después de actualizar
                navigateBackToPendingTasks()
            }
            .addOnFailureListener { e ->
                // Mostrar error
                Snackbar.make(binding.root, "Error al actualizar: ${e.message}", Snackbar.LENGTH_LONG).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}