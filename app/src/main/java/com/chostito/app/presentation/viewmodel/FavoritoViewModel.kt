package com.chostito.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chostito.app.data.remote.dto.FavoritoDto
import com.chostito.app.data.repository.FavoritoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritoViewModel @Inject constructor(
    private val favoritoRepository: FavoritoRepository
) : ViewModel() {

    private val _favoritos = MutableStateFlow<List<FavoritoDto>>(emptyList())
    val favoritos: StateFlow<List<FavoritoDto>> = _favoritos

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadFavoritos()
    }

    fun loadFavoritos() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = favoritoRepository.getFavoritos()
            _isLoading.value = false
            result.onSuccess { _favoritos.value = it }
                .onFailure { _error.value = it.message }
        }
    }

    fun addFavorito(eventoId: Int) {
        viewModelScope.launch {
            val result = favoritoRepository.addFavorito(eventoId)
            result.onSuccess { loadFavoritos() }
        }
    }

    fun removeFavorito(eventoId: Int) {
        viewModelScope.launch {
            val result = favoritoRepository.removeFavorito(eventoId)
            result.onSuccess { loadFavoritos() }
        }
    }

    fun isFavorito(eventoId: Int): Boolean {
        return _favoritos.value.any { it.eventoId == eventoId }
    }

    fun clearError() { _error.value = null }
}
