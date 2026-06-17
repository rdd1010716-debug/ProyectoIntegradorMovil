package com.example.chostitomobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

    Scaffold(
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text(
                text = "Panel de Control",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Primary,
                letterSpacing = 1.sp
            )
            Text(
                text = "Hola, ${user?.nombre?.split(" ")?.firstOrNull() ?: "Administrador"}",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            // Tarjetas de Estadísticas Pro
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCardPro("Eventos", "${stats?.totalEventos ?: 0}", Icons.Default.Event, Primary, Modifier.weight(1f))
                StatCardPro("Ganancia", "Bs ${stats?.totalRecaudado?.toInt() ?: 0}", Icons.Default.Payments, Success, Modifier.weight(1f))
            }
            
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Gestión de Negocio",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            val menuItems = listOf(
                AdminMenuItem("Mis Eventos", Icons.Default.ConfirmationNumber, Primary),
                AdminMenuItem("Ventas", Icons.Default.BarChart, Secondary),
                AdminMenuItem("Usuarios", Icons.Default.People, Success),
                AdminMenuItem("Lugares", Icons.Default.LocationOn, Warning),
                AdminMenuItem("Categorías", Icons.Default.Category, PrimaryLight),
                AdminMenuItem("Ajustes", Icons.Default.Settings, TextSecondary)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(menuItems) { item ->
                    AdminMenuCard(item)
                }
            }
        }
    }
}

data class AdminMenuItem(val title: String, val icon: ImageVector, val color: Color)

@Composable
private fun StatCardPro(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Black, color = TextPrimary)
            Text(label, fontSize = 12.sp, color = TextSecondary)
        }
    }
}

@Composable
private fun AdminMenuCard(item: AdminMenuItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Surface(
                color = item.color.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    item.icon,
                    contentDescription = null,
                    tint = item.color,
                    modifier = Modifier.padding(8.dp).size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(item.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
    }
}
