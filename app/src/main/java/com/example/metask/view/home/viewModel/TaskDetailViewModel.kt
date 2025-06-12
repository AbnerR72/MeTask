package com.example.metask.view.home.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.metask.core.ResultWrapper
import com.example.metask.model.Task
import com.example.metask.network.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel(){
    private val _loaderState = MutableLiveData<Boolean>()
    val loaderState: LiveData<Boolean>
        get() = _loaderState

    private val _taskInfo = MutableLiveData<Task?>()
    val taskInfo: LiveData<Task?>
        get() = _taskInfo

    private val _selectedTask = MutableLiveData<Task?>()
    val selectedTask: LiveData<Task?>
        get() = _selectedTask

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    private val _taskList = MutableLiveData<List<Task>>()
    val taskList: LiveData<List<Task>>
        get() = _taskList

    fun setSelectedTask(task: Task) {
        _selectedTask.postValue(task)
    }

    fun loadTask() {
        _selectedTask.value?.let { task ->
            viewModelScope.launch {
                _loaderState.postValue(true)
                when (val result = repository.getTask(task.id)) {
                    is ResultWrapper.Success -> _selectedTask.postValue(result.data)
                    is ResultWrapper.Error -> _selectedTask.postValue(null)
                }
                _loaderState.postValue(false)
            }
        }
    }

    fun deleteTask(taskId: String) {
        _loaderState.value = true
        viewModelScope.launch {
            when (val result = repository.deleteTask(taskId)) {
                is ResultWrapper.Success -> {
                    _taskList.value = _taskList.value?.filter { it.id != taskId } // ✅ Elimina solo la tarea específica de la lista
                    _loaderState.value = false
                }
                is ResultWrapper.Error -> {
                    _loaderState.value = false
                    _errorMessage.value = result.exception.message ?: "Error desconocido al eliminar tarea"
                }
            }
        }
    }
}