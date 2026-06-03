package com.chostito.app.presentation.screens.client

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
import androidx.navigation.NavController
import com.chostito.app.presentation.components.GlassCard
import com.chostito.app.presentation.components.LoadingIndicator
import com.chostito.app.presentation.components.NeonButton
import com.chostito.app.presentation.navigation.Screen
import com.chostito.app.presentation.theme.CyanNeon
import com.chostito.app.presentation.theme.TextPrimary
import com.chostito.app.presentation.theme.TextSecondary
import com.chostito.app.presentation.viewmodel.ReservaViewModel

@Composable
fun MisReservasScreen(
    navController: NavController,
    viewModel: ReservaViewModel = hiltViewModel()
) {
    val reservas by viewModel.reservas.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadMisReservas() }

    if (isLoading && reservas.isEmpty()) {
        LoadingIndicator()
    } else if (reservas.isEmpty()) {
        Text(
            "No tienes reservas",
            color = TextSecondary,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxSize().padding(24.dp)
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(reservas) { reserva ->
                GlassCard {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            reserva.evento?.titulo ?: "Reserva #${reserva.id}",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary
                        )
                        Text("Estado: ${reserva.estado ?: ""}", color = TextSecondary)
                        Text("Total: Bs. ${reserva.total ?: 0.0}", color = TextSecondary)
                        Text("Entradas: ${reserva.cantidadEntradas ?: 0}", color = TextSecondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        if (reserva.estado == "Pendiente") {
                            NeonButton(
                                text = "Pagar",
                                onClick = { navController.navigate(Screen.Pago.createRoute(reserva.id)) }
                            )
                        } else if (reserva.estado == "Confirmada") {
                            NeonButton(
                                text = "Ver Entradas",
                                onClick = { navController.navigate(Screen.Entradas.createRoute(reserva.id)) }
                            )
                        }
                    }
                }
            }
        }
    }
}
