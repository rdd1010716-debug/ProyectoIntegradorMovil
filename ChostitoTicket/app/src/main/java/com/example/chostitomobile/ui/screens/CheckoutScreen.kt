package com.example.chostitomobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chostitomobile.data.model.*
import com.example.chostitomobile.data.network.SupabaseClient
import com.example.chostitomobile.data.repository.ReservaRepository
import com.example.chostitomobile.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun CheckoutScreen(
    evento: Evento,
    entradas: List<Pair<Entrada, Int>>,
    asientos: List<Asiento>,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var step by remember { mutableStateOf(0) } // 0: resumen, 1: QR, 2: exito
    var reservaId by remember { mutableStateOf<Int?>(null) }
    var qrData by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val cliente = SupabaseClient(LocalContext.current)
    val reservaRepo = ReservaRepository(cliente)

    val total = entradas.sumOf { it.first.precio * it.second } + asientos.sumOf { asiento ->
        entradas.firstOrNull { it.first.id == asiento.idEntrada }?.first?.precio ?: 0.0
    }
    val totalItems = entradas.sumOf { it.second } + asientos.size

    val handleCrearReserva = {
        scope.launch {
            loading = true
            error = null
            try {
                val items = entradas.filter { it.second > 0 }.map { (entrada, cantidad) ->
                    ReservaItemPayload(
                        idEvento = entrada.idEvento,
                        tipo = entrada.tipo,
                        cantidad = cantidad,
                        precio = entrada.precio
                    )
                }
                val idsEntradas = asientos.map { it.id }
                val result = reservaRepo.crearReserva(items, idsEntradas)
                if (result.error != null) {
                    error = result.error
                } else {
                    reservaId = result.id
                    val qr = reservaRepo.generarQR(result.id!!)
                    qrData = qr.qrData
                    step = 1
                }
            } catch (e: Exception) {
                error = e.message
            } finally {
                loading = false
            }
        }
    }

    val handlePagar = {
        scope.launch {
            loading = true
            error = null
            try {
                reservaId?.let { id ->
                    val result = reservaRepo.simularPago(id)
                    if (result.error != null) {
                        error = result.error
                    } else {
                        step = 2
                    }
                }
            } catch (e: Exception) {
                error = e.message
            } finally {
                loading = false
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = TextPrimary)
            }

            Text("Checkout", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(evento.titulo, fontSize = 16.sp, color = TextSecondary)

            Spacer(modifier = Modifier.height(16.dp))

            // Step indicator
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                StepIndicator(number = 1, label = "Resumen", active = step >= 0, completed = step > 0)
                StepIndicator(number = 2, label = "Pago QR", active = step >= 1, completed = step > 1)
                StepIndicator(number = 3, label = "Confirmado", active = step >= 2)
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (error != null) {
                Text(error!!, color = Error, modifier = Modifier.padding(bottom = 16.dp))
            }

            if (step == 0) {
                // Resumen
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Surface)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Detalle de entradas", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        entradas.forEach { (entrada, cantidad) ->
                            if (cantidad > 0) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("${entrada.tipo} x$cantidad", color = TextSecondary)
                                    Text("Bs ${(entrada.precio * cantidad).toInt()}", color = TextPrimary)
                                }
                            }
                        }
                        if (asientos.isNotEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("VIP x${asientos.size}", color = TextSecondary)
                                Text("Bs ${asientos.sumOf { entradas.firstOrNull { e -> e.first.id == it.idEntrada }?.first?.precio ?: 0.0 }.toInt()}", color = TextPrimary)
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = SurfaceLight)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Bs ${total.toInt()}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Secondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { handleCrearReserva() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    enabled = !loading
                ) {
                    if (loading) {
                        CircularProgressIndicator(color = TextPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Crear reserva", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else if (step == 1) {
                // Pago QR
                Text("Código QR de pago", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("Escanea con tu app de banco para pagar", fontSize = 14.sp, color = TextSecondary)

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .size(250.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(androidx.compose.ui.graphics.Color.White)
                        .align(Alignment.CenterHorizontally),
                    contentAlignment = Alignment.Center
                ) {
                    com.example.chostitomobile.ui.components.QRCodeView(
                        content = qrData ?: "QR-PAY-${reservaId ?: 0}"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Monto: Bs ${total.toInt()}", fontSize = 18.sp, color = Secondary, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { handlePagar() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Success),
                    enabled = !loading
                ) {
                    if (loading) {
                        CircularProgressIndicator(color = TextPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Confirmar pago", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else if (step == 2) {
                // Exito
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("✓", fontSize = 64.sp, color = Success)
                        Text("¡Pago exitoso!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Tus entradas están confirmadas", fontSize = 14.sp, color = TextSecondary)

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = onSuccess,
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary)
                        ) {
                            Text("Ver mis entradas", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepIndicator(number: Int, label: String, active: Boolean, completed: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(if (active) Primary else SurfaceLight),
            contentAlignment = Alignment.Center
        ) {
            Text(
                if (completed) "✓" else number.toString(),
                color = if (active) TextPrimary else TextSecondary,
                fontWeight = FontWeight.Bold
            )
        }
        Text(label, fontSize = 10.sp, color = if (active) Primary else TextSecondary)
    }
}
