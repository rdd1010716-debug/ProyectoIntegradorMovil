package com.chostito.app.presentation.screens.client

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.chostito.app.presentation.components.LoadingIndicator
import com.chostito.app.presentation.components.NeonButton
import com.chostito.app.presentation.navigation.Screen
import com.chostito.app.presentation.theme.SuccessGreen
import com.chostito.app.presentation.theme.TextPrimary
import com.chostito.app.presentation.viewmodel.PagoViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.journeyapps.barcodescanner.BarcodeEncoder

@Composable
fun PagoScreen(
    navController: NavController,
    reservaId: Int,
    viewModel: PagoViewModel = hiltViewModel()
) {
    val qrPayload by viewModel.qrPayload.collectAsState()
    val pagoCompletado by viewModel.pagoCompletado.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(reservaId) {
        viewModel.generarQr(reservaId)
    }

    LaunchedEffect(pagoCompletado) {
        if (pagoCompletado) {
            viewModel.resetPago()
            navController.navigate(Screen.Entradas.createRoute(reservaId)) {
                popUpTo(Screen.Home.route) { inclusive = false }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.align(Alignment.Start)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
        }
        Text("Simulación de Pago", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading || qrPayload == null) {
            LoadingIndicator()
        } else {
            val payloadStr = qrPayload?.payload ?: ""
            if (payloadStr.isNotBlank()) {
                val bitmap = rememberQrBitmap(payloadStr)
                bitmap?.let {
                    Image(bitmap = it.asImageBitmap(), contentDescription = "QR Pago", modifier = Modifier.size(250.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Escanea este código para simular el pago", color = TextPrimary)
            }
            Spacer(modifier = Modifier.height(24.dp))
            NeonButton(
                text = "Confirmar Pago Realizado",
                onClick = { viewModel.pagarReserva(reservaId) }
            )
        }
    }
}

@Composable
private fun rememberQrBitmap(content: String): android.graphics.Bitmap? {
    return androidx.compose.runtime.remember(content) {
        try {
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512)
            val encoder = BarcodeEncoder()
            encoder.createBitmap(bitMatrix)
        } catch (e: Exception) {
            null
        }
    }
}
