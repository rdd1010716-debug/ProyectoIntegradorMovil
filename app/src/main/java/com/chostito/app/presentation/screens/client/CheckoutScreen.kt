package com.chostito.app.presentation.screens.client

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.chostito.app.presentation.components.GlassCard
import com.chostito.app.presentation.components.LoadingIndicator
import com.chostito.app.presentation.components.NeonButton
import com.chostito.app.presentation.navigation.Screen
import com.chostito.app.presentation.theme.TextPrimary
import com.chostito.app.presentation.theme.TextSecondary
import com.chostito.app.presentation.viewmodel.EventoViewModel
import com.chostito.app.presentation.viewmodel.ReservaViewModel

@Composable
fun CheckoutScreen(
    navController: NavController,
    eventoId: Int,
    eventoViewModel: EventoViewModel = hiltViewModel(),
    reservaViewModel: ReservaViewModel = hiltViewModel()
) {
    val evento by eventoViewModel.eventoSeleccionado.collectAsState()
    val entradas by eventoViewModel.entradas.collectAsState()
    val reservaCreada by reservaViewModel.reservaCreada.collectAsState()
    val isLoading by reservaViewModel.isLoading.collectAsState()

    var cantidades by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }

    LaunchedEffect(eventoId) {
        eventoViewModel.getEventoById(eventoId)
        eventoViewModel.getEntradasEvento(eventoId)
    }

    LaunchedEffect(reservaCreada) {
        reservaCreada?.let {
            reservaViewModel.clearReservaCreada()
            navController.navigate(Screen.Pago.createRoute(it.id))
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
        }
        Text("Reservar Entradas", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
        Spacer(modifier = Modifier.height(16.dp))

        if (evento == null) {
            LoadingIndicator()
        } else {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                entradas.forEach { tipo ->
                    val cantidad = cantidades[tipo.tipo] ?: 0
                    GlassCard(modifier = Modifier.padding(vertical = 8.dp)) {
                        Column {
                            Text("${tipo.tipo} - Bs. ${tipo.precio}", color = TextPrimary)
                            Text("Disponibles: ${tipo.cantidadDisponible}", color = TextSecondary)
                            Spacer(modifier = Modifier.height(8.dp))
                            // Selector simple +/-
                            androidx.compose.foundation.layout.Row(
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                            ) {
                                IconButton(onClick = {
                                    if (cantidad > 0) cantidades = cantidades + (tipo.tipo to cantidad - 1)
                                }) { Text("-", color = TextPrimary) }
                                Text("$cantidad", color = TextPrimary, modifier = Modifier.padding(horizontal = 12.dp))
                                IconButton(onClick = {
                                    if (cantidad < tipo.cantidadDisponible) cantidades = cantidades + (tipo.tipo to cantidad + 1)
                                }) { Text("+", color = TextPrimary) }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                NeonButton(
                    text = if (isLoading) "Creando..." else "Confirmar Reserva",
                    onClick = {
                        val seleccionadas = cantidades.filter { it.value > 0 }.toList()
                        if (seleccionadas.isNotEmpty()) {
                            reservaViewModel.crearReserva(eventoId, seleccionadas)
                        }
                    },
                    enabled = !isLoading && cantidades.any { it.value > 0 }
                )
            }
        }
    }
}
