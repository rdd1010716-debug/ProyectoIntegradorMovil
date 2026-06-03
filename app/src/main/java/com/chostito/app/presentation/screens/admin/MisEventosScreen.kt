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
import androidx.navigation.NavController
import com.chostito.app.presentation.components.GlassCard
import com.chostito.app.presentation.components.LoadingIndicator
import com.chostito.app.presentation.components.NeonButton
import com.chostito.app.presentation.navigation.Screen
import com.chostito.app.presentation.theme.TextPrimary
import com.chostito.app.presentation.theme.TextSecondary
import com.chostito.app.presentation.viewmodel.EventoViewModel

@Composable
fun MisEventosScreen(
    navController: NavController,
    viewModel: EventoViewModel = hiltViewModel()
) {
    val eventos by viewModel.eventos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) { viewModel.getMisEventos() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Mis Eventos", style = MaterialTheme.typography.headlineLarge, color = TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        NeonButton(
            text = "+ Crear Nuevo Evento",
            onClick = { navController.navigate(Screen.EventoForm.route) }
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading && eventos.isEmpty()) {
            LoadingIndicator()
        } else if (eventos.isEmpty()) {
            Text("No has creado eventos aún", color = TextSecondary)
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(eventos) { evento ->
                    GlassCard {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(evento.titulo, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                            Text("${evento.fecha ?: ""} • ${evento.estado ?: ""}", color = TextSecondary)
                            Text("Lugar: ${evento.lugar?.nombre ?: ""}", color = TextSecondary)
                        }
                    }
                }
            }
        }
    }
}
