package com.chostito.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chostito.app.data.remote.dto.DashboardStatsDto
import com.chostito.app.data.remote.dto.EscanearQrResponse
import com.chostito.app.data.remote.dto.VentaResumenDto
import com.chostito.app.data.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

    private val _stats = MutableStateFlow<DashboardStatsDto?>(null)
    val stats: StateFlow<DashboardStatsDto?> = _stats

    private val _ventas = MutableStateFlow<List<VentaResumenDto>>(emptyList())
    val ventas: StateFlow<List<VentaResumenDto>> = _ventas

    private val _scanResult = MutableStateFlow<EscanearQrResponse?>(null)
    val scanResult: StateFlow<EscanearQrResponse?> = _scanResult

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadStats() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = dashboardRepository.getStats()
            _isLoading.value = false
            result.onSuccess { _stats.value = it }
                .onFailure { _error.value = it.message }
        }
    }

    fun loadVentas() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = dashboardRepository.getMisVentas()
            _isLoading.value = false
            result.onSuccess { _ventas.value = it }
                .onFailure { _error.value = it.message }
        }
    }

    fun loadGanancias() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = dashboardRepository.getTodasGanancias()
            _isLoading.value = false
            result.onSuccess { _ventas.value = it }
                .onFailure { _error.value = it.message }
        }
    }

    fun escanearQr(codigo: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = dashboardRepository.escanearQr(codigo)
            _isLoading.value = false
            result.onSuccess { _scanResult.value = it }
                .onFailure { _error.value = it.message }
        }
    }

    fun clearScanResult() { _scanResult.value = null }
    fun clearError() { _error.value = null }
}
