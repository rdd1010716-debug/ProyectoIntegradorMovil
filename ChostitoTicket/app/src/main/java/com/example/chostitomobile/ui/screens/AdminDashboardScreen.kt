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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chostitomobile.data.model.Perfil
import com.example.chostitomobile.data.network.SupabaseClient
import com.example.chostitomobile.data.repository.AdminRepository
import com.example.chostitomobile.ui.theme.*
import com.example.chostitomobile.ui.viewmodel.AdminViewModel

@Composable
fun AdminDashboardScreen(user: Perfil?) {
    val client = SupabaseClient(LocalContext.current)
    val repository = AdminRepository(client)
    val viewModel: AdminViewModel = viewModel(factory = AdminViewModel.Factory(repository))
    val stats by viewModel.stats
    val loading by viewModel.loading

    LaunchedEffect(Unit) {
        viewModel.cargarStats()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(24.dp)
    ) {
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Primary)
        } else {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "Panel de control",
                    fontSize = 16.sp,
                    color = TextSecondary
                )
                Text(
                    text = "Hola, ${user?.nombre?.split(" ")?.firstOrNull() ?: "Admin"}",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = if (user?.rol == "Admin") "Administrador" else "Organizador",
                    fontSize = 14.sp,
                    color = Primary,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Stats cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatCard("Eventos", "${stats?.totalEventos ?: 0}", "🎪")
                    StatCard("Entradas", "${stats?.entradasVendidas ?: 0}", "🎫")
                    StatCard("Bs", "${stats?.totalRecaudado?.toInt() ?: 0}", "💰")
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Acciones rápidas",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                ActionButton("📂 Categorías", Primary)
                Spacer(modifier = Modifier.height(8.dp))
                ActionButton("🏢 Lugares", Secondary)
                Spacer(modifier = Modifier.height(8.dp))
                if (user?.rol == "Admin") {
                    ActionButton("👥 Usuarios", PrimaryLight)
                    Spacer(modifier = Modifier.height(8.dp))
                    ActionButton("📊 Ganancias", Success)
                }
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, icon: String) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(icon, fontSize = 24.sp)
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(label, fontSize = 10.sp, color = TextSecondary)
        }
    }
}

@Composable
private fun ActionButton(text: String, color: androidx.compose.ui.graphics.Color) {
    Button(
        onClick = { },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Text(text, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}
