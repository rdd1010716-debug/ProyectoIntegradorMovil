package com.chostito.app.domain.model

import com.chostito.app.data.remote.dto.LugarDto

data class Lugar(
    val id: Int,
    val nombre: String,
    val direccion: String? = null,
    val ciudad: String? = null,
    val pais: String? = null,
    val imagenUrl: String? = null
)

fun LugarDto.toDomain(): Lugar = Lugar(
    id = id,
    nombre = nombre,
    direccion = direccion,
    ciudad = ciudad,
    pais = pais,
    imagenUrl = imagenUrl
)
