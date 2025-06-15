package com.example.metask.view.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.metask.databinding.FragmentPendingTaskBinding
import com.example.metask.utils.FragmentCommunicator
import com.example.metask.R


import androidx.recyclerview.widget.LinearLayoutManager
import com.example.metask.model.Task
import com.example.metask.view.home.adapters.TaskAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.util.Date


class PendingTaskFragment : Fragment() {
    private var _binding: FragmentPendingTaskBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private lateinit var taskAdapter: TaskAdapter
    private var taskListener: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPendingTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()
        setupRecyclerView()
        setupClickListeners()
        loadTasks()
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            emptyList(),
            { taskId -> navigateToUpdateTask(taskId) },
            { taskId -> deleteTask(taskId) }
        )

        binding.recycleView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = taskAdapter
        }
    }

    private fun setupClickListeners() {
        binding.iconAdd.setOnClickListener {
            findNavController().navigate(R.id.action_pendingTaskFragment_to_newTaskFragment)
        }
    }

    private fun loadTasks() {
        taskListener = db.collection("tasks")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("PendingTaskFragment", "Error al cargar tareas", error)
                    return@addSnapshotListener
                }

                val tasks = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Task(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            description = doc.getString("description") ?: "",
                            date = doc.getString("date") ?: "" // Usamos string en lugar de Date
                        )
                    } catch (e: Exception) {
                        Log.e("PendingTaskFragment", "Error al parsear tarea ${doc.id}", e)
                        null
                    }
                } ?: emptyList()

                taskAdapter.updateTasks(tasks)
            }
    }

    private fun navigateToUpdateTask(taskId: String) {
        val bundle = Bundle().apply {
            putString("taskId", taskId)
        }
        findNavController().navigate(
            R.id.action_pendingTaskFragment_to_updateTaskFragment,
            bundle
        )
    }

    private fun deleteTask(taskId: String) {
        db.collection("tasks").document(taskId)
            .delete()
            .addOnFailureListener { e ->
                // Mostrar error
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        taskListener?.remove()
        _binding = null
    }
}