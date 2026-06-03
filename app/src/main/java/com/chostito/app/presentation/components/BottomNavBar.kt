package com.chostito.app.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.chostito.app.presentation.navigation.Screen
import com.chostito.app.presentation.theme.CyanNeon
import com.chostito.app.presentation.theme.GlassSurface
import com.chostito.app.presentation.theme.PurpleNeon
import com.chostito.app.presentation.theme.TextSecondary

@Composable
fun ClientBottomBar(navController: NavController) {
    val items = listOf(
        Triple(Screen.Home, "Inicio", Icons.Default.Home),
        Triple(Screen.Favoritos, "Favoritos", Icons.Default.Favorite),
        Triple(Screen.MisReservas, "Reservas", Icons.Default.Receipt),
        Triple(Screen.Perfil, "Perfil", Icons.Default.Person)
    )
    BottomBarContent(navController, items)
}

@Composable
fun AdminBottomBar(navController: NavController) {
    val items = listOf(
        Triple(Screen.Dashboard, "Stats", Icons.Default.BarChart),
        Triple(Screen.MisEventos, "Eventos", Icons.Default.Event),
        Triple(Screen.EscanearQR, "Escanear", Icons.Default.QrCodeScanner),
        Triple(Screen.Ganancias, "Ganancias", Icons.Default.BarChart),
        Triple(Screen.Perfil, "Perfil", Icons.Default.Person)
    )
    BottomBarContent(navController, items)
}

@Composable
private fun BottomBarContent(
    navController: NavController,
    items: List<Triple<Screen, String, androidx.compose.ui.graphics.vector.ImageVector>>
) {
    NavigationBar(
        containerColor = GlassSurface.copy(alpha = 0.9f),
        tonalElevation = 0.dp
    ) {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry.value?.destination?.route

        items.forEach { (screen, label, icon) ->
            val selected = currentRoute == screen.route || currentRoute?.startsWith(screen.route.replace("/{", "")) == true
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = label, tint = if (selected) CyanNeon else TextSecondary) },
                label = { Text(label, color = if (selected) CyanNeon else TextSecondary) },
                selected = selected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = PurpleNeon.copy(alpha = 0.3f),
                    selectedIconColor = CyanNeon,
                    selectedTextColor = CyanNeon,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                )
            )
        }
    }
}
