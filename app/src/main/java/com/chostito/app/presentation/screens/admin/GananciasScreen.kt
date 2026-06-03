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
fun GananciasScreen(viewModel: DashboardViewModel = hiltViewModel()) {
    val ventas by viewModel.ventas.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadGanancias() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Ganancias", style = MaterialTheme.typography.headlineLarge, color = TextPrimary)
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading && ventas.isEmpty()) {
            LoadingIndicator()
        } else if (ventas.isEmpty()) {
            Text("No hay datos de ganancias", color = TextSecondary)
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(ventas) { venta ->
                    GlassCard {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(venta.tituloEvento ?: "Evento #${venta.eventoId}", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                            Text("Entradas vendidas: ${venta.entradasVendidas ?: 0}", color = TextSecondary)
                            Text("Ingresos: Bs. ${venta.ingresos ?: 0.0}", color = CyanNeon)
                        }
                    }
                }
            }
        }
    }
}
