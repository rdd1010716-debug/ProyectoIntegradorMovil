package com.chostito.app.domain.model

import com.chostito.app.data.remote.dto.ReservaDto

data class Reserva(
    val id: Int,
    val fechaReserva: String? = null,
    val total: Double? = null,
    val cantidadEntradas: Int? = null,
    val estado: String? = null,
    val evento: Evento? = null
)

fun ReservaDto.toDomain(): Reserva = Reserva(
    id = id,
    fechaReserva = fechaReserva,
    total = total,
    cantidadEntradas = cantidadEntradas,
    estado = estado,
    evento = evento?.toDomain()
)
