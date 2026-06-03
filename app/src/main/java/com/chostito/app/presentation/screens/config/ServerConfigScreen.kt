package com.chostito.app.presentation.screens.config

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.chostito.app.presentation.components.NeonButton
import com.chostito.app.presentation.theme.CyanNeon
import com.chostito.app.presentation.theme.DarkBackground
import com.chostito.app.presentation.theme.PurpleNeon
import com.chostito.app.presentation.theme.TextPrimary
import com.chostito.app.presentation.theme.TextSecondary
import com.chostito.app.presentation.viewmodel.AuthViewModel

@Composable
fun ServerConfigScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var serverUrl by remember { mutableStateOf("192.168.1.50:5027") }

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
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = serverUrl,
            onValueChange = { serverUrl = it },
            label = { Text("IP y Puerto del backend", color = TextSecondary) },
            placeholder = { Text("Ej: 192.168.1.50:5027", color = TextSecondary.copy(alpha = 0.5f)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = PurpleNeon,
                unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f),
                focusedLabelColor = PurpleNeon,
                unfocusedLabelColor = TextSecondary
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Asegúrate de que tu PC y celular están en la misma red WiFi.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        NeonButton(
            text = "Conectar",
            onClick = { viewModel.saveServerUrl(serverUrl) }
        )
    }
}
