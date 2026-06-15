package com.example.chostitomobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.chostitomobile.data.model.*
import com.example.chostitomobile.data.network.SupabaseClient
import com.example.chostitomobile.data.repository.FavoritoRepository
import com.example.chostitomobile.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun FavoritosScreen(
    onEventoClick: (Int) -> Unit
) {
    val scope = rememberCoroutineScope()
    var favoritos by remember { mutableStateOf<List<Favorito>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val cliente = SupabaseClient(LocalContext.current)
    val favRepo = FavoritoRepository(cliente)

    fun cargar() {
        scope.launch {
            loading = true
            try {
                favoritos = favRepo.getFavoritos()
            } catch (e: Exception) {
                error = e.message
            } finally {
                loading = false
            }
        }
    }

    LaunchedEffect(Unit) { cargar() }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                "Favoritos",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(16.dp)
            )

            if (loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            } else if (error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(error!!, color = Error)
                }
            } else if (favoritos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Sin favoritos", color = TextSecondary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(favoritos) { fav ->
                        FavoritoCard(
                            favorito = fav,
                            onClick = { onEventoClick(fav.evento.id) },
                            onRemove = {
                                scope.launch {
                                    favRepo.eliminarFavorito(fav.evento.id)
                                    cargar()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoritoCard(
    favorito: Favorito,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!favorito.evento.imagenUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = favorito.evento.imagenUrl,
                    contentDescription = favorito.evento.titulo,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    favorito.evento.categoria ?: "Evento",
                    fontSize = 12.sp,
                    color = Primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    favorito.evento.titulo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    "${favorito.evento.lugares?.nombre ?: favorito.evento.lugar ?: "Sin lugar"}, ${favorito.evento.lugares?.ciudad ?: favorito.evento.ciudad ?: ""}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Text(
                    favorito.evento.fecha,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Error)
            }
        }
    }
}
