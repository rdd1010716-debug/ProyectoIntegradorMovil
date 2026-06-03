package com.chostito.app.presentation.screens.auth

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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.chostito.app.presentation.components.LoadingIndicator
import com.chostito.app.presentation.components.NeonButton
import com.chostito.app.presentation.theme.CyanNeon
import com.chostito.app.presentation.theme.PurpleNeon
import com.chostito.app.presentation.theme.TextPrimary
import com.chostito.app.presentation.theme.TextSecondary
import com.chostito.app.presentation.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf("Cliente") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("CHOSTITO", style = MaterialTheme.typography.headlineLarge, color = PurpleNeon, textAlign = TextAlign.Center)
        Text("Crear Cuenta", style = MaterialTheme.typography.titleLarge, color = CyanNeon, modifier = Modifier.padding(bottom = 24.dp))

        if (isLoading) {
            LoadingIndicator()
        } else {
            NeonTextField(value = nombre, onValueChange = { nombre = it }, label = "Nombre")
            Spacer(modifier = Modifier.height(12.dp))
            NeonTextField(value = email, onValueChange = { email = it }, label = "Email")
            Spacer(modifier = Modifier.height(12.dp))
            NeonTextField(value = telefono, onValueChange = { telefono = it }, label = "Teléfono")
            Spacer(modifier = Modifier.height(12.dp))
            NeonTextField(value = password, onValueChange = { password = it }, label = "Contraseña", isPassword = true)
            Spacer(modifier = Modifier.height(12.dp))
            NeonTextField(value = rol, onValueChange = { rol = it }, label = "Rol (Cliente u Organizador)")
            if (error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(error!!, color = androidx.compose.ui.graphics.Color.Red, textAlign = TextAlign.Center)
            }
            Spacer(modifier = Modifier.height(24.dp))
            NeonButton(text = "Registrarse", onClick = { viewModel.register(nombre, email, password, telefono, rol) })
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = { navController.popBackStack() }) {
                Text("¿Ya tienes cuenta? Inicia sesión", color = CyanNeon)
            }
        }
    }
}

@Composable
private fun NeonTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TextSecondary) },
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedBorderColor = PurpleNeon,
            unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f)
        )
    )
}
