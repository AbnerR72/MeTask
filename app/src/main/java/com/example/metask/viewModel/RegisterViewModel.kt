package com.example.metask.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RegisterViewModel: ViewModel() {

    private val _loaderState = MutableLiveData<Boolean>()
    val loaderState: LiveData<Boolean>
        get() = _loaderState
    private val firebase = FirebaseAuth.getInstance()

    fun requestSignUp(email: String, password: String) {
        _loaderState.value = false
        viewModelScope.launch {
            val result = firebase.createUserWithEmailAndPassword(email, password).await()
            _loaderState.value = true
            result.user?.let {
                Log.i("Firebase", "Se ha generado su usuario")
            } ?: run {
                Log.e("Firebase", "Se ha generado un error")
            }
        }
    }

}