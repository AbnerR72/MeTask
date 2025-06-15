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
    private val storageFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Formato para almacenar
    private val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Formato para mostrar

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

                    // Manejo de la fecha como String
                    val dateString = document.getString("date") ?: storageFormat.format(Date())
                    try {
                        val date = storageFormat.parse(dateString) ?: Date()
                        calendar.time = date
                        binding.FechaTIK.setText(displayFormat.format(date))
                    } catch (e: Exception) {
                        calendar.time = Date()
                        binding.FechaTIK.setText(dateString) // Mostrar el string original si falla el parsing
                    }
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
        binding.FechaTIK.setText(displayFormat.format(calendar.time))
    }

    private fun updateTask() {
        val name = binding.nombreTIK.text.toString().trim()
        val description = binding.descriptionTIK.text.toString().trim()
        val dateString = storageFormat.format(calendar.time) // Convertimos a String para almacenar

        if (name.isEmpty() || description.isEmpty()) {
            Snackbar.make(binding.root, "Nombre y descripción son requeridos", Snackbar.LENGTH_SHORT).show()
            return
        }

        val updates = hashMapOf<String, Any>(
            "name" to name,
            "description" to description,
            "date" to dateString // Guardamos como String
        )

        db.collection("tasks").document(taskId)
            .update(updates)
            .addOnSuccessListener {
                navigateBackToPendingTasks()
            }
            .addOnFailureListener { e ->
                Snackbar.make(binding.root, "Error al actualizar: ${e.message}", Snackbar.LENGTH_LONG).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}