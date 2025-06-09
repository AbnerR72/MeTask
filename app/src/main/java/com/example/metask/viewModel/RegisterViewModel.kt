package com.example.metask.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RegisterViewModel : ViewModel() {
    private val _loaderState = MutableLiveData<Boolean>()
    val loaderState: LiveData<Boolean> get() = _loaderState

    private val _errorState = MutableLiveData<String>()
    val errorState: LiveData<String> get() = _errorState

    private val _signUpSuccess = MutableLiveData<Boolean>()
    val signUpSuccess: LiveData<Boolean> get() = _signUpSuccess

    private val firebase = FirebaseAuth.getInstance()

    fun requestSignUp(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _errorState.value = "Email y contraseña son obligatorios"
            return
        }

        _loaderState.value = true

        viewModelScope.launch {
            try {
                val result = firebase.createUserWithEmailAndPassword(email, password).await()
                _loaderState.value = false
                _signUpSuccess.value = true
            } catch (e: Exception) {
                _loaderState.value = false
                _errorState.value = when (e) {
                    is FirebaseAuthWeakPasswordException -> "La contraseña es muy débil"
                    is FirebaseAuthInvalidCredentialsException -> "Email inválido"
                    is FirebaseAuthUserCollisionException -> "El email ya está registrado"
                    else -> "Error al registrar: ${e.message}"
                }
            }
        }
    }
}