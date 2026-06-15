package com.example.chostitomobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chostitomobile.data.network.SupabaseClient
import com.example.chostitomobile.data.repository.DashboardRepository
import com.example.chostitomobile.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun EscanearQRScreen() {
    val scope = rememberCoroutineScope()
    var codigo by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<com.example.chostitomobile.data.model.QRValidationResult?>(null) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val cliente = SupabaseClient(LocalContext.current)
    val dashboardRepo = DashboardRepository(cliente)

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Escanear entrada",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                "Ingresa el código QR manualmente",
                fontSize = 14.sp,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = codigo,
                onValueChange = { codigo = it; error = null; result = null },
                label = { Text("Código QR", color = TextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = SurfaceLight
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    scope.launch {
                        loading = true
                        error = null
                        try {
                            result = dashboardRepo.escanearQR(codigo)
                        } catch (e: Exception) {
                            error = e.message
                        } finally {
                            loading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                enabled = !loading && codigo.isNotEmpty()
            ) {
                if (loading) {
                    CircularProgressIndicator(color = TextPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("Validar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (error != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Error.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("✗", fontSize = 32.sp, color = Error)
                        Text("Entrada inválida", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Error)
                        Text(error!!, fontSize = 14.sp, color = TextSecondary)
                    }
                }
            }

            if (result != null) {
                val r = result!!
                if (r.valido) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Success.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("✓", fontSize = 32.sp, color = Success)
                            Text("Entrada válida", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Success)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Evento: ${r.evento}", color = TextPrimary)
                            Text("Tipo: ${r.tipo}", color = TextSecondary)
                            Text("Comprador: ${r.comprador}", color = TextSecondary)
                            Text("Email: ${r.emailComprador}", color = TextSecondary)
                            Text("Transacción: ${r.codigoTransaccion}", color = TextSecondary)
                        }
                    }
                }
            }
        }
    }
}
