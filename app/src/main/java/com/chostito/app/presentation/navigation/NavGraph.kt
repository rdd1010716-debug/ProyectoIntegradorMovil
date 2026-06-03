package com.chostito.app.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chostito.app.presentation.components.AdminBottomBar
import com.chostito.app.presentation.components.ClientBottomBar
import com.chostito.app.presentation.screens.admin.DashboardScreen
import com.chostito.app.presentation.screens.admin.EscanearQRScreen
import com.chostito.app.presentation.screens.admin.GananciasScreen
import com.chostito.app.presentation.screens.admin.EventoFormScreen
import com.chostito.app.presentation.screens.admin.MisEventosScreen
import com.chostito.app.presentation.screens.auth.LoginScreen
import com.chostito.app.presentation.screens.auth.RegisterScreen
import com.chostito.app.presentation.screens.client.CheckoutScreen
import com.chostito.app.presentation.screens.client.EntradasScreen
import com.chostito.app.presentation.screens.client.EventoDetalleScreen
import com.chostito.app.presentation.screens.client.FavoritosScreen
import com.chostito.app.presentation.screens.client.HomeScreen
import com.chostito.app.presentation.screens.client.MisReservasScreen
import com.chostito.app.presentation.screens.client.PagoScreen
import com.chostito.app.presentation.screens.client.PerfilScreen
import com.chostito.app.presentation.screens.config.ServerConfigScreen

@Composable
fun ChostitoNavGraph(
    isLoggedIn: Boolean,
    userRole: String?,
    serverConfigured: Boolean,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()

    if (!serverConfigured) {
        NavHost(navController = navController, startDestination = Screen.ServerConfig.route) {
            composable(Screen.ServerConfig.route) {
                ServerConfigScreen(navController = navController)
            }
        }
    } else if (!isLoggedIn) {
        AuthNavGraph(navController = navController)
    } else {
        val isAdmin = userRole == "Admin" || userRole == "Organizador"
        if (isAdmin) {
            AdminNavGraph(navController = navController, onLogout = onLogout)
        } else {
            ClientNavGraph(navController = navController, onLogout = onLogout)
        }
    }
}

@Composable
private fun AuthNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) { LoginScreen(navController = navController) }
        composable(Screen.Register.route) { RegisterScreen(navController = navController) }
    }
}

@Composable
private fun ClientNavGraph(navController: NavHostController, onLogout: () -> Unit) {
    Scaffold(
        bottomBar = { ClientBottomBar(navController = navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen(navController = navController) }
            composable(Screen.Favoritos.route) { FavoritosScreen(navController = navController) }
            composable(Screen.MisReservas.route) { MisReservasScreen(navController = navController) }
            composable(Screen.Perfil.route) { PerfilScreen(navController = navController, onLogout = onLogout) }
            composable(Screen.EventoDetalle.route) { backStackEntry ->
                val eventoId = backStackEntry.arguments?.getString("eventoId")?.toIntOrNull() ?: 0
                EventoDetalleScreen(navController = navController, eventoId = eventoId)
            }
            composable(Screen.Checkout.route) { backStackEntry ->
                val eventoId = backStackEntry.arguments?.getString("eventoId")?.toIntOrNull() ?: 0
                CheckoutScreen(navController = navController, eventoId = eventoId)
            }
            composable(Screen.Pago.route) { backStackEntry ->
                val reservaId = backStackEntry.arguments?.getString("reservaId")?.toIntOrNull() ?: 0
                PagoScreen(navController = navController, reservaId = reservaId)
            }
            composable(Screen.Entradas.route) { backStackEntry ->
                val reservaId = backStackEntry.arguments?.getString("reservaId")?.toIntOrNull() ?: 0
                EntradasScreen(navController = navController, reservaId = reservaId)
            }
        }
    }
}

@Composable
private fun AdminNavGraph(navController: NavHostController, onLogout: () -> Unit) {
    Scaffold(
        bottomBar = { AdminBottomBar(navController = navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) { DashboardScreen() }
            composable(Screen.MisEventos.route) { MisEventosScreen(navController = navController) }
            composable(Screen.EventoForm.route) { EventoFormScreen(navController = navController) }
            composable(Screen.EscanearQR.route) { EscanearQRScreen() }
            composable(Screen.Ganancias.route) { GananciasScreen() }
            composable(Screen.Perfil.route) { PerfilScreen(navController = navController, onLogout = onLogout) }
        }
    }
}
