package com.example.chostitomobile.data.repository

import com.example.chostitomobile.data.model.*
import com.example.chostitomobile.data.network.SupabaseClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.headers
import io.ktor.http.parameters
import kotlinx.serialization.json.Json

class AuthRepository(private val client: SupabaseClient) {

    suspend fun login(email: String, password: String): Perfil? {
        val httpResponse = client.client.post("${SupabaseClient.SUPABASE_URL}/auth/v1/token?grant_type=password") {
            headers {
                remove("Authorization")
                append("apikey", SupabaseClient.SUPABASE_ANON_KEY)
            }
            setBody(mapOf("email" to email, "password" to password))
        }

        val responseBody = httpResponse.body<String>()
        val json = Json { ignoreUnknownKeys = true }
        
        // Log para depuración (puedes verlo en Logcat)
        println("DEBUG SUPABASE: $responseBody")

        val response: LoginResponse = json.decodeFromString(responseBody)

        if (response.error != null || response.access_token.isEmpty()) {
            throw Exception(response.errorDescription ?: response.error ?: "Credenciales inválidas")
        }

        client.setToken(response.access_token)
        
        // Obtener perfil usando la tabla 'perfiles'
        val perfilResponse = client.client.get("${SupabaseClient.SUPABASE_URL}/rest/v1/perfiles") {
            header("Authorization", "Bearer ${response.access_token}")
            header("apikey", SupabaseClient.SUPABASE_ANON_KEY)
            url { 
                parameters.append("id", "eq.${response.user?.id ?: ""}")
                parameters.append("select", "*")
            }
        }

        val perfiles: List<Perfil> = json.decodeFromString(perfilResponse.body())
        val p = perfiles.firstOrNull()
        
        if (p != null) {
            client.setUser(Json.encodeToString(Perfil.serializer(), p))
            return p
        } else {
            throw Exception("Usuario autenticado pero no tiene perfil en la base de datos.")
        }
    }

    suspend fun register(email: String, password: String, nombre: String, rol: String, telefono: String? = null): Boolean {
        val httpResponse = client.client.post("${SupabaseClient.SUPABASE_URL}/auth/v1/signup") {
            headers {
                remove("Authorization")
                append("apikey", SupabaseClient.SUPABASE_ANON_KEY)
            }
            setBody(mapOf(
                "email" to email,
                "password" to password,
                "data" to mapOf("nombre" to nombre, "rol" to rol)
            ))
        }

        val response: LoginResponse = httpResponse.body()
        
        if (response.error != null) {
            throw Exception(response.errorDescription ?: response.error)
        }

        return response.access_token.isNotEmpty()
    }

    suspend fun logout() {
        client.clearAuth()
    }

    suspend fun getToken(): String? {
        return client.getToken()
    }

    suspend fun getUser(): Perfil? {
        val userJson = client.getUserJson() ?: return null
        return Json.decodeFromString(Perfil.serializer(), userJson)
    }
}
