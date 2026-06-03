package com.chostito.app.domain.model

import com.chostito.app.data.remote.dto.EventoDto

data class Evento(
    val id: Int,
    val titulo: String,
    val eslogan: String? = null,
    val descripcion: String? = null,
    val fecha: String? = null,
    val hora: String? = null,
    val imagenUrl: String? = null,
    val estado: String? = null,
    val categoria: Categoria? = null,
    val lugar: Lugar? = null,
    val organizador: Usuario? = null
)

fun EventoDto.toDomain(): Evento = Evento(
    id = id,
    titulo = titulo,
    eslogan = eslogan,
    descripcion = descripcion,
    fecha = fecha,
    hora = hora,
    imagenUrl = imagenUrl,
    estado = estado,
    categoria = categoria?.toDomain(),
    lugar = lugar?.toDomain(),
    organizador = organizador?.toDomain()
)
