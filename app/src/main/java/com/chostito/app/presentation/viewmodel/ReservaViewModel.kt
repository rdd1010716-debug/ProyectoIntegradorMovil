package com.chostito.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chostito.app.data.remote.dto.ReservaDetalleDto
import com.chostito.app.data.repository.ReservaRepository
import com.chostito.app.domain.model.Reserva
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReservaViewModel @Inject constructor(
    private val reservaRepository: ReservaRepository
) : ViewModel() {

    private val _reservas = MutableStateFlow<List<Reserva>>(emptyList())
    val reservas: StateFlow<List<Reserva>> = _reservas

    private val _reservaDetalle = MutableStateFlow<ReservaDetalleDto?>(null)
    val reservaDetalle: StateFlow<ReservaDetalleDto?> = _reservaDetalle

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _reservaCreada = MutableStateFlow<Reserva?>(null)
    val reservaCreada: StateFlow<Reserva?> = _reservaCreada

    fun loadMisReservas() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = reservaRepository.getMisReservas()
            _isLoading.value = false
            result.onSuccess { _reservas.value = it }
                .onFailure { _error.value = it.message }
        }
    }

    fun getReservaDetalle(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = reservaRepository.getReservaDetalle(id)
            _isLoading.value = false
            result.onSuccess { _reservaDetalle.value = it }
                .onFailure { _error.value = it.message }
        }
    }

    fun crearReserva(eventoId: Int, entradas: List<Pair<String, Int>>) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = reservaRepository.crearReserva(eventoId, entradas)
            _isLoading.value = false
            result.onSuccess { _reservaCreada.value = it }
                .onFailure { _error.value = it.message }
        }
    }

    fun cancelarReserva(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = reservaRepository.cancelarReserva(id)
            _isLoading.value = false
            result.onSuccess { loadMisReservas() }
                .onFailure { _error.value = it.message }
        }
    }

    fun clearError() { _error.value = null }
    fun clearReservaCreada() { _reservaCreada.value = null }
}
