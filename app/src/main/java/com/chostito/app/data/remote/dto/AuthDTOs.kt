package com.chostito.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("Email") val email: String,
    @SerializedName("Password") val password: String
)

data class RegisterRequest(
    @SerializedName("Nombre") val nombre: String,
    @SerializedName("Email") val email: String,
    @SerializedName("Password") val password: String,
    @SerializedName("Telefono") val telefono: String,
    @SerializedName("Rol") val rol: String
)

data class LoginResponseDto(
    @SerializedName("Token") val token: String? = null,
    @SerializedName("Id") val id: Int? = null,
    @SerializedName("Nombre") val nombre: String? = null,
    @SerializedName("Email") val email: String? = null,
    @SerializedName("Telefono") val telefono: String? = null,
    @SerializedName("Rol") val rol: String? = null,
    @SerializedName("FotoUrl") val fotoUrl: String? = null,
    @SerializedName("FechaRegistro") val fechaRegistro: String? = null,
    @SerializedName("Mensaje") val mensaje: String? = null
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
    @SerializedName("Email") val email: String
)

data class PasswordResetConfirmRequest(
    @SerializedName("Email") val email: String,
    @SerializedName("Token") val token: String,
    @SerializedName("NewPassword") val newPassword: String
)
