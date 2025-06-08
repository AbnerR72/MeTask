package com.example.metask.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.metask.databinding.FragmentPendingTaskBinding
import com.example.metask.utils.FragmentCommunicator
import com.example.metask.R


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class PendingTaskFragment : Fragment() {

    private var _binding: FragmentPendingTaskBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var communicator: FragmentCommunicator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        communicator = requireActivity() as MainActivity
        _binding = FragmentPendingTaskBinding.inflate(inflater, container, false)
        setupView()
        return binding.root

    }

    private fun setupView(){
        binding.iconAdd.setOnClickListener{
            findNavController().navigate(R.id.action_pendingTaskFragment_to_newTaskFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}