package com.example.metask.view.home.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.metask.model.Task
import com.example.metask.network.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SharedTaskViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel(){
    private val _selectedTask = MutableLiveData<Task?>()
    val selectedTask: LiveData<Task?> get() = _selectedTask

    fun setSelectedTask(task: Task) {
        _selectedTask.postValue(task)
    }
}