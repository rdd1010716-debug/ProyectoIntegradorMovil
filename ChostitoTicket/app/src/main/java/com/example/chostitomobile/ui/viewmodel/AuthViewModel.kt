package com.example.chostitomobile.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.chostitomobile.data.model.Perfil
import com.example.chostitomobile.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _user = mutableStateOf<Perfil?>(null)
    val user: State<Perfil?> = _user

    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    init {
        viewModelScope.launch {
            _user.value = repository.getUser()
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val perfil = repository.login(email, password)
                _user.value = perfil
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al iniciar sesión"
            } finally {
                _loading.value = false
            }
        }
    }

    fun register(email: String, password: String, nombre: String, rol: String, telefono: String? = null) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val success = repository.register(email, password, nombre, rol, telefono)
                if (!success) {
                    _error.value = "Error al registrar"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al registrar"
            } finally {
                _loading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _user.value = null
        }
    }

    fun clearError() {
        _error.value = null
    }

    class Factory(private val repository: AuthRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                return AuthViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
