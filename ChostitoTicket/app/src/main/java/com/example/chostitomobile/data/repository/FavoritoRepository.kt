package com.example.chostitomobile.data.repository

import com.example.chostitomobile.data.model.*
import com.example.chostitomobile.data.network.SupabaseClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.get
import io.ktor.http.parameters

class FavoritoRepository(private val client: SupabaseClient) {

    suspend fun getFavoritos(): List<Favorito> {
        val token = client.getToken() ?: SupabaseClient.SUPABASE_ANON_KEY
        val userId = client.getUserId() ?: return emptyList()
        return client.client.get("${SupabaseClient.SUPABASE_URL}/rest/v1/favoritos") {
            header("Authorization", "Bearer $token")
            header("apikey", SupabaseClient.SUPABASE_ANON_KEY)
            url {
                parameters.append("id_usuario", "eq.$userId")
                parameters.append("select", "*,eventos(*,categorias(nombre),lugares(*))")
            }
        }.body()
    }

    suspend fun agregarFavorito(eventoId: Int) {
        val token = client.getToken() ?: SupabaseClient.SUPABASE_ANON_KEY
        val userId = client.getUserId() ?: return
        client.client.post("${SupabaseClient.SUPABASE_URL}/rest/v1/favoritos") {
            header("Authorization", "Bearer $token")
            header("apikey", SupabaseClient.SUPABASE_ANON_KEY)
            setBody(FavoritoRequest(idUsuario = userId, idEvento = eventoId))
        }
    }

    suspend fun eliminarFavorito(eventoId: Int) {
        val token = client.getToken() ?: SupabaseClient.SUPABASE_ANON_KEY
        val userId = client.getUserId() ?: return
        client.client.post("${SupabaseClient.SUPABASE_URL}/rest/v1/favoritos") {
            header("Authorization", "Bearer $token")
            header("apikey", SupabaseClient.SUPABASE_ANON_KEY)
            url {
                parameters.append("id_usuario", "eq.$userId")
                parameters.append("id_evento", "eq.$eventoId")
            }
        }
    }
}
