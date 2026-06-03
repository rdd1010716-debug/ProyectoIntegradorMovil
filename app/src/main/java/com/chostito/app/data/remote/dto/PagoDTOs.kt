package com.chostito.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PagoDto(
    val id: Int,
    val monto: Double? = null,
    val metodoPago: String? = null,
    val estado: String? = null,
    val fechaPago: String? = null,
    val codigoTransaccion: String? = null,
    val reservaId: Int? = null
)

data class QrPayloadDto(
    val payload: String? = null
)
