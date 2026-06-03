package com.chostito.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val nombre: String,
    val email: String,
    val password: String,
    val telefono: String,
    val rol: String
)

data class LoginResponseDto(
    val token: String? = null,
    val usuario: UserDto? = null,
    val mensaje: String? = null
)

data class UserDto(
    val id: Int,
    val nombre: String,
    val email: String,
    val telefono: String? = null,
    val rol: String,
    val fotoUrl: String? = null
)

data class PasswordResetRequest(
    val email: String
)

data class PasswordResetConfirmRequest(
    val email: String,
    val token: String,
    val newPassword: String
)
