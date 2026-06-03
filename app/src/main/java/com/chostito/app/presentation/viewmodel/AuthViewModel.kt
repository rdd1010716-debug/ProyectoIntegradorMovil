package com.chostito.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chostito.app.data.local.DataStoreManager
import com.chostito.app.data.local.SessionManager
import com.chostito.app.data.remote.ApiProvider
import com.chostito.app.data.remote.dto.LoginResponseDto
import com.chostito.app.data.remote.dto.RegisterRequest
import com.chostito.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val dataStoreManager: DataStoreManager,
    private val sessionManager: SessionManager,
    private val apiProvider: ApiProvider
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole

    private val _serverConfigured = MutableStateFlow(false)
    val serverConfigured: StateFlow<Boolean> = _serverConfigured

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _loginSuccess = MutableStateFlow<LoginResponseDto?>(null)
    val loginSuccess: StateFlow<LoginResponseDto?> = _loginSuccess

    private val _currentUser = MutableStateFlow<com.chostito.app.data.remote.dto.UserDto?>(null)
    val currentUser: StateFlow<com.chostito.app.data.remote.dto.UserDto?> = _currentUser

    init {
        viewModelScope.launch {
            checkServerConfig()
        }
    }

    private suspend fun checkServerConfig() {
        val url = dataStoreManager.serverUrl.first()
        if (!url.isNullOrBlank()) {
            sessionManager.serverUrl = url
            apiProvider.initialize(url)
            _serverConfigured.value = true
            checkSession()
        } else {
            _serverConfigured.value = false
        }
    }

    fun saveServerUrl(url: String) {
        viewModelScope.launch {
            val formattedUrl = if (url.startsWith("http")) url else "http://$url"
            dataStoreManager.saveServerUrl(formattedUrl)
            sessionManager.serverUrl = formattedUrl
            apiProvider.initialize(formattedUrl)
            _serverConfigured.value = true
            checkSession()
        }
    }

    private suspend fun checkSession() {
        val (hasSession, role) = authRepository.checkSession()
        _isLoggedIn.value = hasSession
        _userRole.value = role
        _currentUser.value = sessionManager.currentUser
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = authRepository.login(email, password)
            _isLoading.value = false
            result.onSuccess {
                _loginSuccess.value = it
                _isLoggedIn.value = true
                _userRole.value = it.usuario?.rol
                _currentUser.value = it.usuario
            }.onFailure {
                _error.value = it.message
            }
        }
    }

    fun register(nombre: String, email: String, password: String, telefono: String, rol: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val request = RegisterRequest(nombre, email, password, telefono, rol)
            val result = authRepository.register(request)
            _isLoading.value = false
            result.onSuccess {
                _loginSuccess.value = it
                if (it.token != null) {
                    _isLoggedIn.value = true
                    _userRole.value = it.usuario?.rol
                    _currentUser.value = it.usuario
                }
            }.onFailure {
                _error.value = it.message
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _isLoggedIn.value = false
            _userRole.value = null
            _loginSuccess.value = null
            _currentUser.value = null
        }
    }

    fun clearError() {
        _error.value = null
    }
}
