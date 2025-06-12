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
class PendingTaskViewModel @Inject constructor(
    private val repository: TaskRepository
): ViewModel(){
    private val _taskList = MutableLiveData<List<Task>>()
    val taskList: LiveData<List<Task>>
        get() = _taskList

    private val _loaderState = MutableLiveData<Boolean>()
    val loaderState: LiveData<Boolean>
        get() = _loaderState

    private val _taskInfo = MutableLiveData<List<Task>>()
    val taskInfo: LiveData<List<Task>>
        get() = _taskInfo

    fun loadTasks() {
        viewModelScope.launch {
            _loaderState.postValue(true) // Indica que está cargando

            when (val result = repository.getTaskList()) {
                is ResultWrapper.Success -> _taskInfo.postValue(result.data) // Ya es una lista
                is ResultWrapper.Error -> _taskInfo.postValue(emptyList()) // Manejo de error
            }

            _loaderState.postValue(false) // Termina la carga
        }
    }
}