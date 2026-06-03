package com.chostito.app.presentation.screens.client

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import com.chostito.app.domain.model.toDomain
import com.chostito.app.presentation.components.EventCard
import com.chostito.app.presentation.components.LoadingIndicator
import com.chostito.app.presentation.navigation.Screen
import com.chostito.app.presentation.theme.TextSecondary
import com.chostito.app.presentation.viewmodel.FavoritoViewModel

@Composable
fun FavoritosScreen(
    navController: NavController,
    viewModel: FavoritoViewModel = hiltViewModel()
) {
    val favoritos by viewModel.favoritos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadFavoritos() }

    if (isLoading && favoritos.isEmpty()) {
        LoadingIndicator()
    } else if (favoritos.isEmpty()) {
        Text(
            "No tienes favoritos aún",
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
            items(favoritos) { fav ->
                fav.evento?.let { evtDto ->
                    val evento = evtDto.toDomain()
                    EventCard(
                        evento = evento,
                        isFavorite = true,
                        onFavoriteClick = { viewModel.removeFavorito(evento.id) },
                        onClick = { navController.navigate(Screen.EventoDetalle.createRoute(evento.id)) }
                    )
                }
            }
        }
    }
}
