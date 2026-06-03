package com.chostito.app.presentation.screens.client

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.chostito.app.presentation.components.GlassCard
import com.chostito.app.presentation.components.LoadingIndicator
import com.chostito.app.presentation.components.NeonButton
import com.chostito.app.presentation.navigation.Screen
import com.chostito.app.presentation.theme.CyanNeon
import com.chostito.app.presentation.theme.TextPrimary
import com.chostito.app.presentation.theme.TextSecondary
import com.chostito.app.presentation.viewmodel.EventoViewModel
import com.chostito.app.presentation.viewmodel.FavoritoViewModel

@Composable
fun EventoDetalleScreen(
    navController: NavController,
    eventoId: Int,
    viewModel: EventoViewModel = hiltViewModel(),
    favoritoViewModel: FavoritoViewModel = hiltViewModel()
) {
    val evento by viewModel.eventoSeleccionado.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val favoritos by favoritoViewModel.favoritos.collectAsState()
    val isFav = evento?.let { favoritos.any { f -> f.eventoId == it.id } } ?: false

    LaunchedEffect(eventoId) {
        viewModel.getEventoById(eventoId)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
        }

        if (isLoading || evento == null) {
            LoadingIndicator()
        } else {
            val e = evento!!
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                AsyncImage(
                    model = e.imagenUrl,
                    contentDescription = e.titulo,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
                Spacer(modifier = Modifier.height(16.dp))
                GlassCard {
                    Column {
                        Text(e.titulo, style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
                        Text(e.eslogan ?: "", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Fecha: ${e.fecha ?: "Por definir"} • ${e.hora ?: ""}", color = TextSecondary)
                        Text("Lugar: ${e.lugar?.nombre ?: ""}", color = TextSecondary)
                        Text("Categoría: ${e.categoria?.nombre ?: ""}", color = TextSecondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(e.descripcion ?: "", color = TextSecondary)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                NeonButton(text = "Comprar Entradas", onClick = {
                    navController.navigate(Screen.Checkout.createRoute(e.id))
                })
                Spacer(modifier = Modifier.height(8.dp))
                NeonButton(
                    text = if (isFav) "Quitar de Favoritos" else "Agregar a Favoritos",
                    onClick = {
                        if (isFav) favoritoViewModel.removeFavorito(e.id)
                        else favoritoViewModel.addFavorito(e.id)
                    }
                )
            }
        }
    }
}
