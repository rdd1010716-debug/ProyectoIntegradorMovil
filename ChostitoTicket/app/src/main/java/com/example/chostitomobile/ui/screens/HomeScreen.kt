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
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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

    Scaffold(
        containerColor = Background,
        topBar = {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Eventos disponibles",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        onSearch(it)
                    },
                    placeholder = { Text("¿A dónde quieres ir?", color = TextSecondary) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Primary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = SurfaceLight,
                        focusedContainerColor = Surface,
                        unfocusedContainerColor = Surface
                    )
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Categorías con mejor diseño
            ScrollableTabRow(
                selectedTabIndex = if (selectedCategoria == "Todos") 0 else categorias.indexOfFirst { it.nombre == selectedCategoria } + 1,
                containerColor = Background,
                contentColor = Primary,
                edgePadding = 16.dp,
                divider = {},
                indicator = { tabPositions ->
                    if (tabPositions.isNotEmpty()) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[if (selectedCategoria == "Todos") 0 else categorias.indexOfFirst { it.nombre == selectedCategoria } + 1]),
                            color = Primary
                        )
                    }
                }
            ) {
                Tab(
                    selected = selectedCategoria == "Todos",
                    onClick = { selectedCategoria = "Todos"; viewModel.filtrarPorCategoria("Todos") },
                    text = { Text("Todos", fontWeight = if (selectedCategoria == "Todos") FontWeight.Bold else FontWeight.Normal) }
                )
                categorias.forEach { cat ->
                    Tab(
                        selected = selectedCategoria == cat.nombre,
                        onClick = { selectedCategoria = cat.nombre; viewModel.filtrarPorCategoria(cat.nombre) },
                        text = { Text(cat.nombre, fontWeight = if (selectedCategoria == cat.nombre) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            if (loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            } else if (error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(error, color = Error, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            } else {
                if (eventos.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay eventos en esta categoría", color = TextSecondary)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(eventos) { evento ->
                            EventoCard(evento = evento, onClick = { onEventoClick(evento.id) })
                        }
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
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box {
                if (!evento.imagenUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = evento.imagenUrl,
                        contentDescription = evento.titulo,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                // Badge de categoría
                Surface(
                    modifier = Modifier.padding(12.dp).align(Alignment.TopStart),
                    color = Primary.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = evento.categorias?.nombre ?: evento.categoria ?: "Evento",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = evento.titulo,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "📅 ${evento.fecha}",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Row(
                    modifier = Modifier.padding(top = 12.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📍 ${evento.lugares?.nombre ?: evento.lugar ?: "Lugar no def."}",
                        fontSize = 12.sp,
                        color = TextLight,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "Bs ${evento.precioMinimo.toInt()}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Secondary
                    )
                }
            }
        }
    }
}
