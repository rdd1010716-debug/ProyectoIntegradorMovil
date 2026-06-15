package com.example.chostitomobile.data.network

import android.content.Context
import android.content.SharedPreferences
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class SupabaseClient(context: Context) {

    companion object {
        const val SUPABASE_URL = "https://laxlcygeipperbhwoweg.supabase.co"
        const val SUPABASE_ANON_KEY = "sb_publishable_TNezdy_FzcW9VFR16gWUaQ_FJgOR7N2"
        private const val PREFS_NAME = "chostito_prefs"
        private const val KEY_TOKEN = "supabase_token"
        private const val KEY_USER = "supabase_user"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        install(Logging) {
            level = LogLevel.ALL
        }
        defaultRequest {
            contentType(ContentType.Application.Json)
        }
    }

    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    fun getUserJson(): String? {
        return prefs.getString(KEY_USER, null)
    }

    fun getUserId(): String? {
        val userJson = getUserJson() ?: return null
        return try {
            val json = Json { ignoreUnknownKeys = true }
            val user = json.decodeFromString<com.example.chostitomobile.data.model.Perfil>(userJson)
            user.id
        } catch (e: Exception) {
            null
        }
    }

    fun setToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun setUser(userJson: String) {
        prefs.edit().putString(KEY_USER, userJson).apply()
    }

    fun clearAuth() {
        prefs.edit().remove(KEY_TOKEN).remove(KEY_USER).apply()
    }
}
