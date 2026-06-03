package com.chostito.app.presentation.screens.config

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.chostito.app.presentation.components.GlassTextField
import com.chostito.app.presentation.components.NeonButton
import com.chostito.app.presentation.theme.CyanNeon
import com.chostito.app.presentation.theme.ErrorRed
import com.chostito.app.presentation.theme.PurpleNeon
import com.chostito.app.presentation.theme.TextSecondary
import com.chostito.app.presentation.viewmodel.AuthViewModel

@Composable
fun ServerConfigScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var serverUrl by remember { mutableStateOf("10.0.2.2:5027") }

    LaunchedEffect(error) {
        if (error != null) viewModel.clearError()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "CHOSTITO",
            style = MaterialTheme.typography.headlineLarge,
            color = PurpleNeon,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Configuración del Servidor",
            style = MaterialTheme.typography.titleLarge,
            color = CyanNeon,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Emulador: 10.0.2.2:5027 | Físico: IP de tu PC",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        GlassTextField(
            value = serverUrl,
            onValueChange = { serverUrl = it },
            label = "IP y Puerto del backend"
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(color = PurpleNeon)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Probando conexión...", color = TextSecondary)
        } else {
            if (error != null) {
                Text(
                    text = error!!,
                    color = ErrorRed,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            NeonButton(
                text = "Conectar",
                onClick = { viewModel.saveServerUrl(serverUrl) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Asegúrate de que tu backend esté corriendo (dotnet run).",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}
