package com.example.chostitomobile.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.chostitomobile.data.model.Evento
import com.example.chostitomobile.data.model.Categoria
import com.example.chostitomobile.data.model.Lugar
import com.example.chostitomobile.data.repository.EventoRepository
import kotlinx.coroutines.launch

class EventoViewModel(private val repository: EventoRepository) : ViewModel() {

    private val _eventos = mutableStateOf<List<Evento>>(emptyList())
    val eventos: State<List<Evento>> = _eventos

    private val _allEventos = mutableStateOf<List<Evento>>(emptyList())

    private val _categorias = mutableStateOf<List<Categoria>>(emptyList())
    val categorias: State<List<Categoria>> = _categorias

    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    fun cargarEventos(estado: String? = "Publicado") {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val data = repository.getEventos(estado)
                _allEventos.value = data
                _eventos.value = data
                _categorias.value = repository.getCategorias()
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al cargar eventos"
            } finally {
                _loading.value = false
            }
        }
    }

    fun buscarEventos(query: String) {
        if (query.isEmpty()) {
            _eventos.value = _allEventos.value
            return
        }
        _eventos.value = _allEventos.value.filter {
            it.titulo.contains(query, ignoreCase = true) ||
            it.lugares?.nombre?.contains(query, ignoreCase = true) == true ||
            it.lugar?.contains(query, ignoreCase = true) == true
        }
    }

    fun filtrarPorCategoria(categoria: String) {
        if (categoria == "Todos") {
            _eventos.value = _allEventos.value
            return
        }
        _eventos.value = _allEventos.value.filter { 
            it.categorias?.nombre == categoria || it.categoria == categoria
        }
    }

    class Factory(private val repository: EventoRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EventoViewModel::class.java)) {
                return EventoViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
