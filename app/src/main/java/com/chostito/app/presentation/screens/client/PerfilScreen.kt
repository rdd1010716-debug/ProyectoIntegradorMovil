package com.chostito.app.presentation.screens.client

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.chostito.app.presentation.components.GlassCard
import com.chostito.app.presentation.components.NeonButton
import com.chostito.app.presentation.theme.CyanNeon
import com.chostito.app.presentation.theme.PurpleNeon
import com.chostito.app.presentation.theme.TextPrimary
import com.chostito.app.presentation.theme.TextSecondary
import com.chostito.app.presentation.viewmodel.AuthViewModel

@Composable
fun PerfilScreen(
    navController: NavController,
    onLogout: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val user by viewModel.currentUser.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Perfil",
            tint = PurpleNeon,
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Mi Perfil", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
        Spacer(modifier = Modifier.height(24.dp))

        user?.let { u ->
            GlassCard {
                Column(modifier = Modifier.padding(8.dp)) {
                    InfoRow(label = "Nombre", value = u.nombre)
                    InfoRow(label = "Email", value = u.email)
                    InfoRow(label = "Teléfono", value = u.telefono ?: "No especificado")
                    InfoRow(label = "Rol", value = u.rol)
                }
            }
        } ?: Text("No se pudo cargar el usuario", color = TextSecondary)

        Spacer(modifier = Modifier.height(32.dp))
        NeonButton(
            text = "Cerrar Sesión",
            onClick = onLogout
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
        Text(value, color = CyanNeon, style = MaterialTheme.typography.bodyLarge)
    }
}
