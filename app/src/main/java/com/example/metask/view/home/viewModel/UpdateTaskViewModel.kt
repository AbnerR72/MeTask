package com.example.metask.view.home.viewModel

import android.util.Log
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
class UpdateTaskViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel(){
    private val _loaderState = MutableLiveData<Boolean>()
    val loaderState: LiveData<Boolean>
        get() = _loaderState

    private val _operationSuccess = MutableLiveData<Boolean>()
    val operationSuccess: LiveData<Boolean>
        get() = _operationSuccess

    fun updateTaskInfo(updatedTask: Task) { // ✅ Se recibe un Task completo, no strings separados
        viewModelScope.launch {
            when (val result = repository.updateTask(updatedTask)) {
                is ResultWrapper.Success -> {
                    _operationSuccess.postValue(true) // ✅ Indica éxito
                }
                is ResultWrapper.Error -> {
                    Log.e("TaskViewModel", "Error al actualizar tarea", result.exception)
                }
            }
        }
    }
}