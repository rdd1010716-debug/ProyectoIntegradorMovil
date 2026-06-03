package com.chostito.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CategoriaDto(
    val id: Int,
    val nombre: String,
    val descripcion: String? = null,
    val icono: String? = null
)

data class LugarDto(
    val id: Int,
    val nombre: String,
    val direccion: String? = null,
    val pais: String? = null,
    val ciudad: String? = null,
    val ambiente: String? = null,
    val capacidadTotal: Int? = null,
    val imagenUrl: String? = null
)

data class EventoDto(
    val id: Int,
    val titulo: String,
    val eslogan: String? = null,
    val descripcion: String? = null,
    val fecha: String? = null,
    val hora: String? = null,
    val imagenUrl: String? = null,
    val estado: String? = null,
    val fechaCreacion: String? = null,
    val categoriaId: Int? = null,
    val categoria: CategoriaDto? = null,
    val lugarId: Int? = null,
    val lugar: LugarDto? = null,
    val organizadorId: Int? = null,
    val organizador: UserDto? = null
)

data class EntradaTipoDto(
    val tipo: String,
    val precio: Double,
    val cantidadDisponible: Int
)

data class EventoDetalleDto(
    val evento: EventoDto,
    val entradas: List<EntradaTipoDto>? = null
)
