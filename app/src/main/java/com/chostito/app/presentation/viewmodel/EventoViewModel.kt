package com.chostito.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chostito.app.data.remote.dto.EntradaTipoDto
import com.chostito.app.data.repository.EventoRepository
import com.chostito.app.domain.model.Categoria
import com.chostito.app.domain.model.Evento
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventoViewModel @Inject constructor(
    private val eventoRepository: EventoRepository
) : ViewModel() {

    private val _eventos = MutableStateFlow<List<Evento>>(emptyList())
    val eventos: StateFlow<List<Evento>> = _eventos

    private val _eventoSeleccionado = MutableStateFlow<Evento?>(null)
    val eventoSeleccionado: StateFlow<Evento?> = _eventoSeleccionado

    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categorias: StateFlow<List<Categoria>> = _categorias

    private val _entradas = MutableStateFlow<List<EntradaTipoDto>>(emptyList())
    val entradas: StateFlow<List<EntradaTipoDto>> = _entradas

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadEventos()
        loadCategorias()
    }

    fun loadEventos(categoriaId: Int? = null, busqueda: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = eventoRepository.getEventos(categoriaId, busqueda)
            _isLoading.value = false
            result.onSuccess { _eventos.value = it }
                .onFailure { _error.value = it.message }
        }
    }

    fun loadCategorias() {
        viewModelScope.launch {
            val result = eventoRepository.getCategorias()
            result.onSuccess { _categorias.value = it }
        }
    }

    fun getEventoById(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = eventoRepository.getEventoById(id)
            _isLoading.value = false
            result.onSuccess { _eventoSeleccionado.value = it }
                .onFailure { _error.value = it.message }
        }
    }

    fun getEntradasEvento(id: Int) {
        viewModelScope.launch {
            val result = eventoRepository.getEntradasEvento(id)
            result.onSuccess { _entradas.value = it }
        }
    }

    fun getMisEventos() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = eventoRepository.getMisEventos()
            _isLoading.value = false
            result.onSuccess { _eventos.value = it }
                .onFailure { _error.value = it.message }
        }
    }

    fun createEvento(evento: com.chostito.app.data.remote.dto.EventoDto) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = eventoRepository.createEvento(evento)
            _isLoading.value = false
            result.onSuccess {
                _eventos.value = listOf(it) + _eventos.value
            }.onFailure { _error.value = it.message }
        }
    }

    fun clearError() { _error.value = null }
    fun clearSelection() { _eventoSeleccionado.value = null }
}
