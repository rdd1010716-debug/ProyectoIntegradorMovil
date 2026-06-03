package com.chostito.app.domain.model

import com.chostito.app.data.remote.dto.CategoriaDto

data class Categoria(
    val id: Int,
    val nombre: String,
    val descripcion: String? = null,
    val icono: String? = null
)

fun CategoriaDto.toDomain(): Categoria = Categoria(
    id = id,
    nombre = nombre,
    descripcion = descripcion,
    icono = icono
)
