package com.chostito.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ReservaDto(
    val id: Int,
    val fechaReserva: String? = null,
    val total: Double? = null,
    val cantidadEntradas: Int? = null,
    val estado: String? = null,
    val usuarioId: Int? = null,
    val eventoId: Int? = null,
    val evento: EventoDto? = null
)

data class EntradaDto(
    val id: Int,
    val tipo: String? = null,
    val precio: Double? = null,
    val codigoQR: String? = null,
    val estado: String? = null,
    val numeroAsiento: String? = null,
    val eventoId: Int? = null
)

data class CrearReservaRequest(
    val eventoId: Int,
    val entradas: List<EntradaReservaRequest>
)

data class EntradaReservaRequest(
    val tipo: String,
    val cantidad: Int
)

data class ReservaDetalleDto(
    val reserva: ReservaDto,
    val entradas: List<EntradaDto>? = null,
    val pago: PagoDto? = null
)
