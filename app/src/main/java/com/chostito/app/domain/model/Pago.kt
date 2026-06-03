package com.chostito.app.domain.model

import com.chostito.app.data.remote.dto.PagoDto

data class Pago(
    val id: Int,
    val monto: Double? = null,
    val estado: String? = null,
    val fechaPago: String? = null,
    val codigoTransaccion: String? = null
)

fun PagoDto.toDomain(): Pago = Pago(
    id = id,
    monto = monto,
    estado = estado,
    fechaPago = fechaPago,
    codigoTransaccion = codigoTransaccion
)
