package com.chostito.app.data.remote.dto

data class DashboardStatsDto(
    val totalEventos: Int? = null,
    val totalReservasConfirmadas: Int? = null,
    val totalIngresos: Double? = null,
    val totalUsuarios: Int? = null,
    val totalEntradasVendidas: Int? = null
)

data class VentaResumenDto(
    val eventoId: Int? = null,
    val tituloEvento: String? = null,
    val entradasVendidas: Int? = null,
    val ingresos: Double? = null
)

data class EscanearQrRequest(
    val codigoQR: String
)

data class EscanearQrResponse(
    val valido: Boolean,
    val mensaje: String? = null,
    val entrada: EntradaDto? = null
)
