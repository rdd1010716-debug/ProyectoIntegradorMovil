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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.chostito.app.presentation.components.EventCard
import com.chostito.app.presentation.components.LoadingIndicator
import com.chostito.app.presentation.navigation.Screen
import com.chostito.app.presentation.theme.CyanNeon
import com.chostito.app.presentation.theme.PurpleNeon
import com.chostito.app.presentation.theme.TextPrimary
import com.chostito.app.presentation.theme.TextSecondary
import com.chostito.app.presentation.viewmodel.EventoViewModel
import com.chostito.app.presentation.viewmodel.FavoritoViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: EventoViewModel = hiltViewModel(),
    favoritoViewModel: FavoritoViewModel = hiltViewModel()
) {
    val eventos by viewModel.eventos.collectAsState()
    val categorias by viewModel.categorias.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val favoritos by favoritoViewModel.favoritos.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoria by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadEventos()
        favoritoViewModel.loadFavoritos()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                viewModel.loadEventos(selectedCategoria, if (it.isBlank()) null else it)
            },
            label = { Text("Buscar eventos...", color = TextSecondary) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = PurpleNeon,
                unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f)
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        androidx.compose.foundation.lazy.LazyRow(modifier = Modifier.height(48.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChip(
                    selected = selectedCategoria == null,
                    onClick = {
                        selectedCategoria = null
                        viewModel.loadEventos(busqueda = if (searchQuery.isBlank()) null else searchQuery)
                    },
                    label = { Text("Todos", color = if (selectedCategoria == null) CyanNeon else TextSecondary) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PurpleNeon.copy(alpha = 0.2f)
                    )
                )
            }
            items(categorias) { cat ->
                FilterChip(
                    selected = selectedCategoria == cat.id,
                    onClick = {
                        selectedCategoria = cat.id
                        viewModel.loadEventos(categoriaId = cat.id, busqueda = if (searchQuery.isBlank()) null else searchQuery)
                    },
                    label = { Text(cat.nombre, color = if (selectedCategoria == cat.id) CyanNeon else TextSecondary) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PurpleNeon.copy(alpha = 0.2f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (isLoading && eventos.isEmpty()) {
            LoadingIndicator()
        } else if (eventos.isEmpty()) {
            Text("No hay eventos disponibles", color = TextSecondary, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.fillMaxWidth())
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(eventos) { evento ->
                    val isFav = favoritos.any { it.eventoId == evento.id }
                    EventCard(
                        evento = evento,
                        isFavorite = isFav,
                        onFavoriteClick = {
                            if (isFav) favoritoViewModel.removeFavorito(evento.id)
                            else favoritoViewModel.addFavorito(evento.id)
                        },
                        onClick = { navController.navigate(Screen.EventoDetalle.createRoute(evento.id)) }
                    )
                }
            }
        }
    }
}
