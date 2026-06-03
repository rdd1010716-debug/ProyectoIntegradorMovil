package com.chostito.app.presentation.screens.client

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.chostito.app.presentation.components.GlassCard
import com.chostito.app.presentation.components.LoadingIndicator
import com.chostito.app.presentation.theme.TextPrimary
import com.chostito.app.presentation.theme.TextSecondary
import com.chostito.app.presentation.viewmodel.ReservaViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.journeyapps.barcodescanner.BarcodeEncoder

@Composable
fun EntradasScreen(
    navController: NavController,
    reservaId: Int,
    viewModel: ReservaViewModel = hiltViewModel()
) {
    val detalle by viewModel.reservaDetalle.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(reservaId) {
        viewModel.getReservaDetalle(reservaId)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
        }
        Text("Mis Entradas", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading || detalle == null) {
            LoadingIndicator()
        } else {
            val entradas = detalle!!.entradas ?: emptyList()
            if (entradas.isEmpty()) {
                Text("No hay entradas para mostrar", color = TextSecondary)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(entradas) { entrada ->
                        GlassCard {
                            Column(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Tipo: ${entrada.tipo ?: ""}", color = TextPrimary)
                                Text("Asiento: ${entrada.numeroAsiento ?: "N/A"}", color = TextSecondary)
                                Text("Estado: ${entrada.estado ?: ""}", color = TextSecondary)
                                Spacer(modifier = Modifier.height(12.dp))
                                entrada.codigoQR?.let { code ->
                                    val bitmap = rememberQrBitmapSimple(code)
                                    bitmap?.let {
                                        Image(
                                            bitmap = it.asImageBitmap(),
                                            contentDescription = "QR Entrada",
                                            modifier = Modifier.size(200.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun rememberQrBitmapSimple(content: String): android.graphics.Bitmap? {
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
