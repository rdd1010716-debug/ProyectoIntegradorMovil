package com.chostito.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chostito.app.data.remote.dto.QrPayloadDto
import com.chostito.app.data.repository.PagoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PagoViewModel @Inject constructor(
    private val pagoRepository: PagoRepository
) : ViewModel() {

    private val _qrPayload = MutableStateFlow<QrPayloadDto?>(null)
    val qrPayload: StateFlow<QrPayloadDto?> = _qrPayload

    private val _pagoCompletado = MutableStateFlow(false)
    val pagoCompletado: StateFlow<Boolean> = _pagoCompletado

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun generarQr(reservaId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = pagoRepository.generarQr(reservaId)
            _isLoading.value = false
            result.onSuccess { _qrPayload.value = it }
                .onFailure { _error.value = it.message }
        }
    }

    fun pagarReserva(reservaId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = pagoRepository.pagarReserva(reservaId)
            _isLoading.value = false
            result.onSuccess { _pagoCompletado.value = true }
                .onFailure { _error.value = it.message }
        }
    }

    fun clearError() { _error.value = null }
    fun resetPago() { _pagoCompletado.value = false; _qrPayload.value = null }
}
