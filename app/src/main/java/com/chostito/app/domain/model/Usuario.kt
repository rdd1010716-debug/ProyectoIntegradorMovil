package com.chostito.app.domain.model

import com.chostito.app.data.remote.dto.UserDto

data class Usuario(
    val id: Int,
    val nombre: String,
    val email: String,
    val telefono: String? = null,
    val rol: String,
    val fotoUrl: String? = null
)

fun UserDto.toDomain(): Usuario = Usuario(
    id = id,
    nombre = nombre,
    email = email,
    telefono = telefono,
    rol = rol,
    fotoUrl = fotoUrl
)
