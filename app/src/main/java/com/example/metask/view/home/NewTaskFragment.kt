package com.example.metask.view.home

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.metask.databinding.FragmentNewTaskBinding
import com.example.metask.utils.FragmentCommunicator
import com.example.metask.R


import com.example.metask.model.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID


class NewTaskFragment : Fragment() {
    private var _binding: FragmentNewTaskBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Formato ISO para almacenar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.iconFlecha.setOnClickListener {
            findNavController().navigate(R.id.action_newTaskFragment_to_pendingTaskFragment)
        }

        binding.FechaTIK.setOnClickListener {
            showDatePicker()
        }

        binding.buttonSecond.setOnClickListener {
            addTask()
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
        val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.FechaTIK.setText(displayFormat.format(calendar.time))
    }

    private fun addTask() {
        val name = binding.nombreTIK.text.toString().trim()
        val description = binding.descriptionTIK.text.toString().trim()
        val dateString = dateFormat.format(calendar.time) // Formateamos como String ISO

        if (name.isEmpty() || description.isEmpty()) {
            Snackbar.make(binding.root, "Nombre y descripción son requeridos", Snackbar.LENGTH_SHORT).show()
            return
        }

        val task = hashMapOf(
            "id" to UUID.randomUUID().toString(), // Generamos un ID único
            "name" to name,
            "description" to description,
            "date" to dateString // Guardamos como String
        )

        db.collection("tasks")
            .document(task["id"] as String) // Usamos el ID como documento
            .set(task)
            .addOnSuccessListener {
                findNavController().navigate(R.id.action_newTaskFragment_to_pendingTaskFragment)
            }
            .addOnFailureListener { e ->
                Snackbar.make(binding.root, "Error al agregar tarea: ${e.message}", Snackbar.LENGTH_LONG).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}