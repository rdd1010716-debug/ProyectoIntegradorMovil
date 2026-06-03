package com.chostito.app.presentation.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chostito.app.presentation.components.GlassCard
import com.chostito.app.presentation.components.LoadingIndicator
import com.chostito.app.presentation.theme.CyanNeon
import com.chostito.app.presentation.theme.TextPrimary
import com.chostito.app.presentation.theme.TextSecondary
import com.chostito.app.presentation.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(viewModel: DashboardViewModel = hiltViewModel()) {
    val stats by viewModel.stats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadStats() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Dashboard", style = MaterialTheme.typography.headlineLarge, color = TextPrimary)
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading && stats == null) {
            LoadingIndicator()
        } else {
            stats?.let { s ->
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item { StatCard("Total Eventos", s.totalEventos?.toString() ?: "0") }
                    item { StatCard("Reservas Confirmadas", s.totalReservasConfirmadas?.toString() ?: "0") }
                    item { StatCard("Ingresos Totales", "Bs. ${s.totalIngresos ?: 0.0}") }
                    item { StatCard("Usuarios Registrados", s.totalUsuarios?.toString() ?: "0") }
                    item { StatCard("Entradas Vendidas", s.totalEntradasVendidas?.toString() ?: "0") }
                }
            } ?: Text("No hay datos disponibles", color = TextSecondary)
        }
    }
}

@Composable
private fun StatCard(title: String, value: String) {
    GlassCard {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
            Text(value, style = MaterialTheme.typography.headlineMedium, color = CyanNeon)
        }
    }
}
