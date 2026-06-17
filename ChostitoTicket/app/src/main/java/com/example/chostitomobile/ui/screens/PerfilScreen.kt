package com.example.chostitomobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chostitomobile.data.model.Perfil
import com.example.chostitomobile.ui.theme.*

@Composable
fun PerfilScreen(
    user: Perfil?,
    onLogout: () -> Unit
) {
    val scrollState = rememberScrollState()
    Scaffold(
        containerColor = Background
    ) { padding ->
        if (user == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Inicia sesión para ver tu perfil", color = TextSecondary)
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                // Header con degradado
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Primary, Background)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Surface)
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(PrimaryLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = user.nombre.firstOrNull()?.uppercase() ?: "U",
                                    fontSize = 42.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(user.nombre, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                        Surface(
                            color = Primary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                user.rol.uppercase(),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                fontSize = 10.sp,
                                color = Primary,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                    Text("INFORMACIÓN PERSONAL", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 0.5.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    ProfileOption(Icons.Default.Email, "Email", user.email)
                    ProfileOption(Icons.Default.Phone, "Teléfono", user.telefono ?: "No especificado")
                    ProfileOption(Icons.Default.CalendarMonth, "Miembro desde", user.fechaRegistro?.substring(0, 10) ?: "-")
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("PREFERENCIAS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 0.5.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    ProfileOption(Icons.Default.Notifications, "Notificaciones", "Activado", showChevron = true)
                    ProfileOption(Icons.Default.Shield, "Seguridad", "Configurar", showChevron = true)
                    
                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = onLogout,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Error.copy(alpha = 0.15f), contentColor = Error)
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cerrar sesión", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun ProfileOption(icon: ImageVector, label: String, value: String, showChevron: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = Surface,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.size(44.dp)
        ) {
            Icon(icon, contentDescription = null, tint = Primary, modifier = Modifier.padding(10.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 12.sp, color = TextSecondary)
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
        if (showChevron) {
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextLight)
        }
    }
}
