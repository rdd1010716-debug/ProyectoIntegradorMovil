package com.chostito.app.presentation.screens.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.chostito.app.presentation.components.NeonButton
import com.chostito.app.presentation.theme.TextPrimary
import com.chostito.app.presentation.theme.TextSecondary
import com.chostito.app.presentation.viewmodel.EventoViewModel

@Composable
fun EventoFormScreen(
    navController: NavController,
    viewModel: EventoViewModel = hiltViewModel()
) {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var imagenUrl by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
        }
        Text("Nuevo Evento", style = MaterialTheme.typography.headlineLarge, color = TextPrimary)
        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            AdminTextField(value = titulo, onValueChange = { titulo = it }, label = "Título")
            Spacer(modifier = Modifier.height(12.dp))
            AdminTextField(value = descripcion, onValueChange = { descripcion = it }, label = "Descripción")
            Spacer(modifier = Modifier.height(12.dp))
            AdminTextField(value = fecha, onValueChange = { fecha = it }, label = "Fecha (YYYY-MM-DD)")
            Spacer(modifier = Modifier.height(12.dp))
            AdminTextField(value = hora, onValueChange = { hora = it }, label = "Hora")
            Spacer(modifier = Modifier.height(12.dp))
            AdminTextField(value = imagenUrl, onValueChange = { imagenUrl = it }, label = "URL Imagen")
            Spacer(modifier = Modifier.height(24.dp))
            NeonButton(
                text = "Crear Evento",
                onClick = {
                    val evento = com.chostito.app.data.remote.dto.EventoDto(
                        id = 0,
                        titulo = titulo,
                        descripcion = descripcion,
                        fecha = fecha,
                        hora = hora,
                        imagenUrl = imagenUrl
                    )
                    viewModel.createEvento(evento)
                    navController.popBackStack()
                }
            )
        }
    }
}

@Composable
private fun AdminTextField(value: String, onValueChange: (String) -> Unit, label: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TextSecondary) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedBorderColor = com.chostito.app.presentation.theme.PurpleNeon,
            unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f)
        )
    )
}
