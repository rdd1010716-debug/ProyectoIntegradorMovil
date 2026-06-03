package com.chostito.app.data.repository

import com.chostito.app.data.local.DataStoreManager
import com.chostito.app.data.local.SessionManager
import com.chostito.app.data.remote.ApiProvider
import com.chostito.app.data.remote.dto.LoginRequest
import com.chostito.app.data.remote.dto.LoginResponseDto
import com.chostito.app.data.remote.dto.RegisterRequest
import com.chostito.app.data.remote.dto.UserDto
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiProvider: ApiProvider,
    private val dataStoreManager: DataStoreManager,
    private val sessionManager: SessionManager
) {
    suspend fun login(email: String, password: String): Result<LoginResponseDto> {
        return try {
            val response = apiProvider.getApi().login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                body.token?.let { token ->
                    dataStoreManager.saveToken(token)
                    sessionManager.jwtToken = token
                }
                body.id?.let {
                    val user = UserDto(
                        id = it,
                        nombre = body.nombre ?: "",
                        email = body.email ?: "",
                        telefono = body.telefono,
                        rol = body.rol ?: "Cliente",
                        fotoUrl = body.fotoUrl
                    )
                    dataStoreManager.saveUserJson(Gson().toJson(user))
                    sessionManager.currentUser = user
                }
                Result.success(body)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Error de login"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(request: RegisterRequest): Result<LoginResponseDto> {
        return try {
            val response = apiProvider.getApi().register(request)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                body.token?.let { token ->
                    dataStoreManager.saveToken(token)
                    sessionManager.jwtToken = token
                }
                body.id?.let {
                    val user = UserDto(
                        id = it,
                        nombre = body.nombre ?: "",
                        email = body.email ?: "",
                        telefono = body.telefono,
                        rol = body.rol ?: "Cliente",
                        fotoUrl = body.fotoUrl
                    )
                    dataStoreManager.saveUserJson(Gson().toJson(user))
                    sessionManager.currentUser = user
                }
                Result.success(body)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Error de registro"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        dataStoreManager.clearAll()
        sessionManager.clear()
    }

    suspend fun checkSession(): Pair<Boolean, String?> {
        val token = dataStoreManager.token.first()
        val userJson = dataStoreManager.userJson.first()
        return if (!token.isNullOrBlank() && !userJson.isNullOrBlank()) {
            sessionManager.jwtToken = token
            val user = Gson().fromJson(userJson, UserDto::class.java)
            sessionManager.currentUser = user
            Pair(true, user?.rol)
        } else {
            Pair(false, null)
        }
    }
}
