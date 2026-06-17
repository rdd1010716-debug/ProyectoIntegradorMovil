package com.example.chostitomobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.chostitomobile.data.model.*
import com.example.chostitomobile.data.network.SupabaseClient
import com.example.chostitomobile.data.repository.EventoRepository
import com.example.chostitomobile.data.repository.FavoritoRepository
import com.example.chostitomobile.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun EventoDetalleScreen(
    eventoId: Int,
    user: Perfil?,
    onBack: () -> Unit,
    onComprar: (evento: Evento, entradas: List<Pair<Entrada, Int>>, asientos: List<Asiento>) -> Unit
) {
    val scope = rememberCoroutineScope()
    var evento by remember { mutableStateOf<Evento?>(null) }
    var entradas by remember { mutableStateOf<List<Entrada>>(emptyList()) }
    var asientos by remember { mutableStateOf<List<Asiento>>(emptyList()) }
    var cantidades by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) }
    var selectedAsientos by remember { mutableStateOf<List<Asiento>>(emptyList()) }
    var isFav by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val cliente = SupabaseClient(LocalContext.current)
    val eventoRepo = EventoRepository(cliente)
    val favRepo = FavoritoRepository(cliente)

    LaunchedEffect(eventoId) {
        scope.launch {
            try {
                evento = eventoRepo.getEventoById(eventoId)
                entradas = eventoRepo.getEntradas(eventoId)
                asientos = eventoRepo.getAsientos(eventoId)
                cantidades = entradas.associate { it.id to 0 }
                if (user != null) {
                    val favs = favRepo.getFavoritos()
                    isFav = favs.any { it.evento.id == eventoId }
                }
            } catch (e: Exception) {
                error = e.message
            } finally {
                loading = false
            }
        }
    }

    if (loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Primary)
        }
        return
    }

    if (evento == null || error != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(error ?: "Evento no encontrado", color = Error)
        }
        return
    }

    val ev = evento!!
    val esCliente = user?.rol == "Cliente"
    val totalEntradas = entradas.filter { it.tipo != "VIP" }.sumOf { cantidades[it.id] ?: 0 }
    val totalAsientos = selectedAsientos.size
    val totalItems = totalEntradas + totalAsientos
    val total = entradas.sumOf { (cantidades[it.id] ?: 0) * it.precio } + selectedAsientos.sumOf { asiento ->
        entradas.firstOrNull { it.id == asiento.idEntrada }?.precio ?: 0.0
    }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Imagen
            Box(modifier = Modifier.fillMaxWidth().height(240.dp)) {
                if (!ev.imagenUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = ev.imagenUrl,
                        contentDescription = ev.titulo,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                // Back button
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = TextPrimary)
                }
                // Fav button
                if (user != null) {
                    IconButton(
                        onClick = {
                            scope.launch {
                                try {
                                    if (isFav) favRepo.eliminarFavorito(eventoId)
                                    else favRepo.agregarFavorito(eventoId)
                                    isFav = !isFav
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        },
                        modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                    ) {
                        Icon(
                            if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = if (isFav) Error else TextPrimary
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(ev.categorias?.nombre ?: ev.categoria ?: "Evento", fontSize = 12.sp, color = Primary, fontWeight = FontWeight.Bold)
                Text(ev.titulo, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                if (!ev.eslogan.isNullOrEmpty()) {
                    Text(ev.eslogan, fontSize = 14.sp, color = TextSecondary, modifier = Modifier.padding(top = 4.dp))
                }
                Text("📅 ${ev.fecha} ${ev.hora ?: ""}", fontSize = 14.sp, color = TextSecondary, modifier = Modifier.padding(top = 8.dp))
                Text("📍 ${ev.lugares?.nombre ?: ev.lugar ?: ""}, ${ev.lugares?.ciudad ?: ev.ciudad ?: ""}", fontSize = 14.sp, color = TextSecondary)

                if (!ev.descripcion.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Acerca del evento", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(ev.descripcion, fontSize = 14.sp, color = TextSecondary, modifier = Modifier.padding(top = 4.dp))
                }

                // Entradas
                Spacer(modifier = Modifier.height(24.dp))
                Text("Entradas", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

                entradas.forEach { entrada ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(entrada.tipo, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("Bs ${entrada.precio.toInt()}", fontSize = 14.sp, color = Secondary)
                                Text("${entrada.cantidadDisponible} disponibles", fontSize = 12.sp, color = TextSecondary)
                            }
                            if (esCliente && entrada.tipo != "VIP") {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            val current = cantidades[entrada.id] ?: 0
                                            if (current > 0) {
                                                cantidades = cantidades.toMutableMap().apply { put(entrada.id, current - 1) }
                                            }
                                        }
                                    ) {
                                        Text("-", fontSize = 20.sp, color = Primary)
                                    }
                                    Text(
                                        "${cantidades[entrada.id] ?: 0}",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                    IconButton(
                                        onClick = {
                                            val current = cantidades[entrada.id] ?: 0
                                            if (current < entrada.cantidadDisponible) {
                                                cantidades = cantidades.toMutableMap().apply { put(entrada.id, current + 1) }
                                            }
                                        }
                                    ) {
                                        Text("+", fontSize = 20.sp, color = Primary)
                                    }
                                }
                            } else if (entrada.tipo == "VIP") {
                                Text("Selecciona asientos", fontSize = 12.sp, color = TextSecondary)
                            }
                        }
                    }
                }

                // Asientos VIP
                if (asientos.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Asientos VIP", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    val asientosPorSeccion = asientos.groupBy { it.seccion }
                    asientosPorSeccion.forEach { (seccion, lista) ->
                        Text(seccion, fontSize = 14.sp, color = TextSecondary, modifier = Modifier.padding(vertical = 8.dp))
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            lista.forEach { asiento ->
                                val isSelected = selectedAsientos.contains(asiento)
                                val isOcupado = asiento.estado == "Ocupada"
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            when {
                                                isOcupado -> Error.copy(alpha = 0.3f)
                                                isSelected -> Primary
                                                else -> SurfaceLight
                                            }
                                        )
                                        .clickable(enabled = !isOcupado && esCliente) {
                                            if (isSelected) {
                                                selectedAsientos = selectedAsientos - asiento
                                            } else {
                                                selectedAsientos = selectedAsientos + asiento
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        asiento.numero,
                                        fontSize = 12.sp,
                                        color = if (isSelected) TextPrimary else TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        // Bottom bar con total y botón
        if (esCliente) {
            Surface(
                modifier = Modifier.align(Alignment.BottomCenter),
                color = Surface,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total", fontSize = 12.sp, color = TextSecondary)
                        Text("Bs ${total.toInt()}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Secondary)
                        Text("$totalItems entradas", fontSize = 12.sp, color = TextSecondary)
                    }
                    Button(
                        onClick = {
                            val items = entradas.filter { (cantidades[it.id] ?: 0) > 0 && it.tipo != "VIP" }
                                .map { it to (cantidades[it.id] ?: 0) }
                            onComprar(ev, items, selectedAsientos)
                        },
                        enabled = totalItems > 0,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Text("Comprar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            Surface(
                modifier = Modifier.align(Alignment.BottomCenter),
                color = Surface,
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No disponible para tu rol", color = TextSecondary)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        content()
    }
}
