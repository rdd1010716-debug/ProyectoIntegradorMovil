package com.example.chostitomobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chostitomobile.data.model.Evento
import com.example.chostitomobile.data.network.SupabaseClient
import com.example.chostitomobile.data.repository.EventoRepository
import com.example.chostitomobile.ui.theme.*
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext

@Composable
fun AdminEventosScreen() {
    val scope = rememberCoroutineScope()
    var eventos by remember { mutableStateOf<List<Evento>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    
    val cliente = SupabaseClient(LocalContext.current)
    val repo = EventoRepository(cliente)

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                eventos = repo.getEventos()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                loading = false
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { }, containerColor = Primary) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo Evento", tint = TextPrimary)
            }
        },
        containerColor = Background
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            Text("Mis Eventos", fontSize = 24.sp, color = TextPrimary)
            
            if (loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 16.dp)) {
                    items(eventos) { evento ->
                        Card(colors = CardDefaults.cardColors(containerColor = Surface)) {
                            ListItem(
                                headlineContent = { Text(evento.titulo, color = TextPrimary) },
                                supportingContent = { Text(evento.estado, color = if (evento.estado == "Publicado") Success else Warning) },
                                colors = ListItemDefaults.colors(containerColor = Surface)
                            )
                        }
                    }
                }
            }
        }
    }
}
