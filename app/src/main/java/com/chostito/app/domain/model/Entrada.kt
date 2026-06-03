package com.chostito.app.domain.model

import com.chostito.app.data.remote.dto.EntradaDto

data class Entrada(
    val id: Int,
    val tipo: String? = null,
    val precio: Double? = null,
    val codigoQR: String? = null,
    val estado: String? = null,
    val numeroAsiento: String? = null
)

fun EntradaDto.toDomain(): Entrada = Entrada(
    id = id,
    tipo = tipo,
    precio = precio,
    codigoQR = codigoQR,
    estado = estado,
    numeroAsiento = numeroAsiento
)
