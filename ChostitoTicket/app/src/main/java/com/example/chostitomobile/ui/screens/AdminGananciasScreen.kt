package com.example.chostitomobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chostitomobile.ui.theme.*
import androidx.compose.ui.platform.LocalContext
import com.example.chostitomobile.data.network.SupabaseClient
import com.example.chostitomobile.data.repository.AdminRepository
import kotlinx.coroutines.launch

@Composable
fun AdminGananciasScreen() {
    val scope = rememberCoroutineScope()
    var total by remember { mutableStateOf(0.0) }
    var loading by remember { mutableStateOf(true) }

    val cliente = SupabaseClient(LocalContext.current)
    val repo = AdminRepository(cliente)

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                total = repo.getStats().totalRecaudado
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                loading = false
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        if (loading) {
            CircularProgressIndicator(color = Primary)
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Ganancias Totales", fontSize = 20.sp, color = TextSecondary)
                Text("Bs $total", fontSize = 48.sp, color = Success, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            }
        }
    }
}
