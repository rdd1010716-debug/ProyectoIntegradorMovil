package com.example.chostitomobile.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.chostitomobile.data.model.DashboardStats
import com.example.chostitomobile.data.repository.AdminRepository
import kotlinx.coroutines.launch

class AdminViewModel(private val repository: AdminRepository) : ViewModel() {

    private val _stats = mutableStateOf<DashboardStats?>(null)
    val stats: State<DashboardStats?> = _stats

    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    fun cargarStats() {
        viewModelScope.launch {
            _loading.value = true
            try {
                _stats.value = repository.getStats()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loading.value = false
            }
        }
    }

    class Factory(private val repository: AdminRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AdminViewModel(repository) as T
        }
    }
}
