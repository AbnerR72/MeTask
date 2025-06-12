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
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class NewTaskViewModel @Inject constructor(
    private val repository: TaskRepository
): ViewModel(){
    private val _loaderState = MutableLiveData<Boolean>()
    val loaderState: LiveData<Boolean>
        get() = _loaderState

    private val _operationSuccess = MutableLiveData<Boolean>()
    val operationSuccess: LiveData<Boolean>
        get() = _operationSuccess

    fun createTaskInfo(taskId: String, name: String, description: String, bornDate: Date) {
        val task = Task(id = taskId, name, description, bornDate)
        _loaderState.value = true
        viewModelScope.launch {
            when (val result = repository.createTask(task)) {
                is ResultWrapper.Success -> {
                    _loaderState.value = false
                    _operationSuccess.value = true
                }

                is ResultWrapper.Error -> {
                    _loaderState.value = false
                    val errorMessage = result.exception.message
                }
            }
        }
    }
}