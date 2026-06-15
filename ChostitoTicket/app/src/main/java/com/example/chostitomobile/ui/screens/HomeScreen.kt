package com.example.chostitomobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.chostitomobile.data.model.Evento
import com.example.chostitomobile.ui.theme.*
import com.example.chostitomobile.ui.viewmodel.EventoViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    viewModel: EventoViewModel,
    onEventoClick: (Int) -> Unit,
    onSearch: (String) -> Unit
) {
    val eventos = viewModel.eventos.value
    val categorias = viewModel.categorias.value
    val loading = viewModel.loading.value
    val error = viewModel.error.value
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoria by remember { mutableStateOf("Todos") }

    LaunchedEffect(Unit) {
        viewModel.cargarEventos()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Text(
                text = "Eventos disponibles",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(16.dp)
            )

            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    onSearch(it)
                },
                placeholder = { Text("Buscar eventos...", color = TextSecondary) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Primary) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = SurfaceLight
                )
            )

            // Categorías
            if (categorias.isNotEmpty()) {
            PrimaryScrollableTabRow(
                selectedTabIndex = if (selectedCategoria == "Todos") 0 else categorias.indexOfFirst { it.nombre == selectedCategoria } + 1,
                containerColor = Background,
                contentColor = Primary,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                    Tab(
                        selected = selectedCategoria == "Todos",
                        onClick = { selectedCategoria = "Todos"; viewModel.filtrarPorCategoria("Todos") },
                        text = { Text("Todos") }
                    )
                    categorias.forEach { cat ->
                        Tab(
                            selected = selectedCategoria == cat.nombre,
                            onClick = { selectedCategoria = cat.nombre; viewModel.filtrarPorCategoria(cat.nombre) },
                            text = { Text(cat.nombre) }
                        )
                    }
                }
            }

            if (loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Primary)
                }
            } else if (error != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(error, color = Error, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            } else if (eventos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay eventos disponibles", color = TextSecondary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(eventos) { evento ->
                        EventoCard(evento = evento, onClick = { onEventoClick(evento.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun EventoCard(evento: Evento, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            if (!evento.imagenUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = evento.imagenUrl,
                    contentDescription = evento.titulo,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = evento.categoria ?: "Evento",
                    fontSize = 12.sp,
                    color = Primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = evento.titulo,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                if (!evento.eslogan.isNullOrEmpty()) {
                    Text(
                        text = evento.eslogan,
                        fontSize = 14.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${evento.lugares?.nombre ?: evento.lugar ?: "Sin lugar"}, ${evento.lugares?.ciudad ?: evento.ciudad ?: ""}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "Desde Bs ${evento.precioMinimo.toInt()}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Secondary
                    )
                }
            }
        }
    }
}
