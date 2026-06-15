package com.example.chostitomobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chostitomobile.data.model.*
import com.example.chostitomobile.data.network.SupabaseClient
import com.example.chostitomobile.data.repository.ReservaRepository
import com.example.chostitomobile.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun MisReservasScreen(
    onBack: () -> Unit,
    onVerQR: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    var reservas by remember { mutableStateOf<List<Reserva>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var tab by remember { mutableStateOf("Todas") }
    val tabs = listOf("Todas", "Pendiente", "Confirmada", "Cancelada")

    val cliente = SupabaseClient(LocalContext.current)
    val reservaRepo = ReservaRepository(cliente)

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                reservas = reservaRepo.misReservas()
            } catch (e: Exception) {
                error = e.message
            } finally {
                loading = false
            }
        }
    }

    var showQR by remember { mutableStateOf<String?>(null) }

    if (showQR != null) {
        AlertDialog(
            onDismissRequest = { showQR = null },
            confirmButton = {
                TextButton(onClick = { showQR = null }) { Text("Cerrar") }
            },
            title = { Text("Tu entrada") },
            text = {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            modifier = Modifier.size(150.dp),
                            color = androidx.compose.ui.graphics.Color.White,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            com.example.chostitomobile.ui.components.QRCodeView(content = showQR!!)
                        }
                        Text("Escanea este código en el evento", fontSize = 12.sp, color = TextSecondary, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = TextPrimary)
                }
                Text("Mis Reservas", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }

            // Tabs
            PrimaryScrollableTabRow(
                selectedTabIndex = tabs.indexOf(tab),
                containerColor = Background,
                contentColor = Primary,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                tabs.forEach { t ->
                    Tab(
                        selected = tab == t,
                        onClick = { tab = t },
                        text = { Text(t) }
                    )
                }
            }

            if (loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            } else if (error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(error!!, color = Error)
                }
            } else {
                val filtered = if (tab == "Todas") reservas else reservas.filter { it.estado == tab }
                if (filtered.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Sin reservas", color = TextSecondary)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filtered) { reserva ->
                            ReservaCard(
                                reserva = reserva,
                                onVerQR = { showQR = it },
                                onCancelar = {
                                    scope.launch {
                                        reservaRepo.cancelarReserva(reserva.id)
                                        reservas = reservas.map { if (it.id == reserva.id) it.copy(estado = "Cancelada") else it }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReservaCard(
    reserva: Reserva,
    onVerQR: (String) -> Unit,
    onCancelar: () -> Unit
) {
    val statusColor = when (reserva.estado) {
        "Pendiente" -> Warning
        "Confirmada" -> Success
        "Cancelada" -> Error
        else -> TextSecondary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    reserva.estado,
                    fontSize = 12.sp,
                    color = statusColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(statusColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
                Text(
                    reserva.fechaReserva?.substring(0, 10) ?: "",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            reserva.items.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "${item.entradas?.tipo ?: "Entrada"} ${item.asientos?.numero?.let { "(Asiento $it)" } ?: ""}",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                        Text(
                            item.entradas?.eventos?.titulo ?: "",
                            fontSize = 12.sp,
                            color = TextLight
                        )
                    }
                    if (reserva.estado == "Confirmada" && item.entradasVendidas.isNotEmpty()) {
                        IconButton(onClick = { onVerQR(item.entradasVendidas.first().codigoQr) }) {
                            Icon(Icons.Default.QrCode, contentDescription = "Ver QR", tint = Primary)
                        }
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = SurfaceLight)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${reserva.cantidadEntradas} entradas",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Text(
                    "Bs ${reserva.total.toInt()}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Secondary
                )
            }

            if (reserva.estado == "Pendiente") {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onCancelar,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Error)
                ) {
                    Text("Cancelar", fontSize = 14.sp)
                }
            }
        }
    }
}
