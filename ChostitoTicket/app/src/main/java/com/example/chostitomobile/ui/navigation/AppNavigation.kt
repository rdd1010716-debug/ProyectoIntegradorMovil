package com.example.chostitomobile.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.chostitomobile.data.model.*
import com.example.chostitomobile.ui.screens.*
import com.example.chostitomobile.ui.viewmodel.AuthViewModel
import com.example.chostitomobile.ui.viewmodel.EventoViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Login : Screen("login", "Login")
    object Register : Screen("register", "Registro")
    object Home : Screen("home", "Explorar", Icons.Default.Home)
    object EventoDetalle : Screen("evento/{eventoId}", "Evento")
    object Checkout : Screen("checkout", "Checkout")
    object MisReservas : Screen("mis_reservas", "Mis Reservas", Icons.Default.ConfirmationNumber)
    object Favoritos : Screen("favoritos", "Favoritos", Icons.Default.Favorite)
    object Perfil : Screen("perfil", "Perfil", Icons.Default.Person)
    object AdminDashboard : Screen("admin", "Dashboard", Icons.Default.Settings)
    object AdminEventos : Screen("admin_eventos", "Mis Eventos", Icons.Default.Star)
    object AdminEscanear : Screen("admin_escanear", "Escanear", Icons.Default.QrCodeScanner)
    object AdminGanancias : Screen("admin_ganancias", "Ganancias", Icons.Default.ShoppingCart)
}

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    eventoViewModel: EventoViewModel,
    user: Perfil?
) {
    val navController = rememberNavController()
    var currentEvento by remember { mutableStateOf<Evento?>(null) }
    var currentEntradas by remember { mutableStateOf<List<Pair<Entrada, Int>>>(emptyList()) }
    var currentAsientos by remember { mutableStateOf<List<Asiento>>(emptyList()) }

    val clienteItems = listOf(Screen.Home, Screen.MisReservas, Screen.Favoritos, Screen.Perfil)
    val organizadorItems = listOf(Screen.AdminDashboard, Screen.AdminEventos, Screen.AdminEscanear, Screen.Perfil)
    val adminItems = listOf(Screen.AdminDashboard, Screen.AdminGanancias, Screen.AdminEventos, Screen.AdminEscanear, Screen.Perfil)

    val bottomNavItems = when (user?.rol) {
        "Cliente" -> clienteItems
        "Organizador" -> organizadorItems
        "Admin" -> adminItems
        else -> emptyList()
    }

    Scaffold(
        bottomBar = {
            if (bottomNavItems.isNotEmpty()) {
                NavigationBar(
                    containerColor = com.example.chostitomobile.ui.theme.Surface
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { screen.icon?.let { Icon(it, contentDescription = screen.title) } },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route?.startsWith(screen.route) == true } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = com.example.chostitomobile.ui.theme.Primary,
                                selectedTextColor = com.example.chostitomobile.ui.theme.Primary,
                                unselectedIconColor = com.example.chostitomobile.ui.theme.TextSecondary,
                                unselectedTextColor = com.example.chostitomobile.ui.theme.TextSecondary,
                                indicatorColor = com.example.chostitomobile.ui.theme.SurfaceLight
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (user == null) Screen.Login.route else {
                if (user.rol == "Cliente") Screen.Home.route else Screen.AdminDashboard.route
            },
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = authViewModel,
                    onLoginSuccess = {
                        val rol = authViewModel.user.value?.rol
                        navController.navigate(
                            if (rol == "Cliente") Screen.Home.route else Screen.AdminDashboard.route
                        ) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) }
                )
            }
            composable(Screen.Register.route) {
                RegisterScreen(
                    viewModel = authViewModel,
                    onRegisterSuccess = { navController.navigate(Screen.Login.route) },
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) }
                )
            }
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = eventoViewModel,
                    onEventoClick = { eventoId ->
                        navController.navigate("evento/$eventoId")
                    },
                    onSearch = { query -> eventoViewModel.buscarEventos(query) }
                )
            }
            composable(Screen.EventoDetalle.route) { backStackEntry ->
                val eventoId = backStackEntry.arguments?.getString("eventoId")?.toIntOrNull() ?: 0
                EventoDetalleScreen(
                    eventoId = eventoId,
                    user = user,
                    onBack = { navController.popBackStack() },
                    onComprar = { evento, entradas, asientos ->
                        currentEvento = evento
                        currentEntradas = entradas
                        currentAsientos = asientos
                        navController.navigate(Screen.Checkout.route)
                    }
                )
            }
            composable(Screen.Checkout.route) {
                if (currentEvento != null) {
                    CheckoutScreen(
                        evento = currentEvento!!,
                        entradas = currentEntradas,
                        asientos = currentAsientos,
                        onBack = { navController.popBackStack() },
                        onSuccess = {
                            navController.navigate(Screen.MisReservas.route) {
                                popUpTo(Screen.Home.route)
                            }
                        }
                    )
                } else {
                    PlaceholderText("No hay items para comprar")
                }
            }
            composable(Screen.MisReservas.route) {
                MisReservasScreen(
                    onBack = { navController.popBackStack() },
                    onVerQR = { codigo ->
                        // Mostrar QR en un dialog o nueva pantalla
                    }
                )
            }
            composable(Screen.Favoritos.route) {
                FavoritosScreen(
                    onEventoClick = { eventoId ->
                        navController.navigate("evento/$eventoId")
                    }
                )
            }
            composable(Screen.Perfil.route) {
                PerfilScreen(
                    user = user,
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.AdminDashboard.route) {
                AdminDashboardScreen(user = user)
            }
            composable(Screen.AdminEventos.route) {
                AdminEventosScreen()
            }
            composable(Screen.AdminEscanear.route) {
                EscanearQRScreen()
            }
            composable(Screen.AdminGanancias.route) {
                AdminGananciasScreen()
            }
        }
    }
}

@Composable
private fun PlaceholderText(text: String) {
    androidx.compose.material3.Text(
        text = text,
        modifier = Modifier.padding(16.dp),
        color = com.example.chostitomobile.ui.theme.TextPrimary
    )
}
