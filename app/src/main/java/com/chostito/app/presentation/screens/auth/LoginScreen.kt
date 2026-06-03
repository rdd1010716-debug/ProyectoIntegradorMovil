package com.chostito.app.presentation.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.chostito.app.presentation.components.GlassTextField
import com.chostito.app.presentation.components.LoadingIndicator
import com.chostito.app.presentation.components.NeonButton
import com.chostito.app.presentation.navigation.Screen
import com.chostito.app.presentation.theme.CyanNeon
import com.chostito.app.presentation.theme.PurpleNeon
import com.chostito.app.presentation.theme.TextPrimary
import com.chostito.app.presentation.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
        Text("CHOSTITO", style = MaterialTheme.typography.headlineLarge, color = PurpleNeon, textAlign = TextAlign.Center)
        Text("Inicia Sesión", style = MaterialTheme.typography.titleLarge, color = CyanNeon, modifier = Modifier.padding(bottom = 24.dp))

        if (isLoading) {
            LoadingIndicator()
        } else {
            GlassTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email"
            )
            Spacer(modifier = Modifier.height(12.dp))
            GlassTextField(
                value = password,
                onValueChange = { password = it },
                label = "Contraseña",
                visualTransformation = PasswordVisualTransformation()
            )
            if (error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(error!!, color = androidx.compose.ui.graphics.Color.Red, textAlign = TextAlign.Center)
            }
            Spacer(modifier = Modifier.height(24.dp))
            NeonButton(text = "Ingresar", onClick = { viewModel.login(email, password) })
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = { navController.navigate(Screen.Register.route) }) {
                Text("¿No tienes cuenta? Regístrate", color = CyanNeon)
            }
        }
    }
}
