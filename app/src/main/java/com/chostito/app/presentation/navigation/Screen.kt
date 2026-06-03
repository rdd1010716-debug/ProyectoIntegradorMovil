package com.chostito.app.presentation.navigation

sealed class Screen(val route: String) {
    object ServerConfig : Screen("server_config")
    object Login : Screen("login")
    object Register : Screen("register")

    // Client
    object Home : Screen("home")
    object EventoDetalle : Screen("evento_detalle/{eventoId}") {
        fun createRoute(eventoId: Int) = "evento_detalle/$eventoId"
    }
    object Favoritos : Screen("favoritos")
    object Checkout : Screen("checkout/{eventoId}") {
        fun createRoute(eventoId: Int) = "checkout/$eventoId"
    }
    object Pago : Screen("pago/{reservaId}") {
        fun createRoute(reservaId: Int) = "pago/$reservaId"
    }
    object MisReservas : Screen("mis_reservas")
    object Entradas : Screen("entradas/{reservaId}") {
        fun createRoute(reservaId: Int) = "entradas/$reservaId"
    }
    object Perfil : Screen("perfil")

    // Admin
    object Dashboard : Screen("dashboard")
    object MisEventos : Screen("mis_eventos")
    object EventoForm : Screen("evento_form")
    object EscanearQR : Screen("escanear_qr")
    object Ganancias : Screen("ganancias")
}
