package com.example.chostitomobile.data.repository

import com.example.chostitomobile.data.model.*
import com.example.chostitomobile.data.network.SupabaseClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.parameters

class EventoRepository(private val client: SupabaseClient) {

    suspend fun getEventos(estado: String? = null): List<Evento> {
        val token = client.getToken() ?: SupabaseClient.SUPABASE_ANON_KEY
        val response = client.client.get("${SupabaseClient.SUPABASE_URL}/rest/v1/eventos") {
            header("Authorization", "Bearer $token")
            header("apikey", SupabaseClient.SUPABASE_ANON_KEY)
            url {
                parameters.append("select", "*,categorias(nombre),lugares(nombre,ciudad)")
                if (estado != null) {
                    parameters.append("estado", "eq.$estado")
                }
            }
        }
        return response.body()
    }

    suspend fun getEventoById(id: Int): Evento? {
        val token = client.getToken() ?: SupabaseClient.SUPABASE_ANON_KEY
        val response = client.client.get("${SupabaseClient.SUPABASE_URL}/rest/v1/eventos") {
            header("Authorization", "Bearer $token")
            header("apikey", SupabaseClient.SUPABASE_ANON_KEY)
            url {
                parameters.append("id", "eq.$id")
                parameters.append("select", "*,categorias(nombre),lugares(nombre,ciudad,direccion)")
            }
        }
        val lista: List<Evento> = response.body()
        return lista.firstOrNull()
    }

    suspend fun getEntradas(idEvento: Int): List<Entrada> {
        val token = client.getToken() ?: SupabaseClient.SUPABASE_ANON_KEY
        return client.client.get("${SupabaseClient.SUPABASE_URL}/rest/v1/entradas") {
            header("Authorization", "Bearer $token")
            header("apikey", SupabaseClient.SUPABASE_ANON_KEY)
            url { parameters.append("id_evento", "eq.$idEvento") }
        }.body()
    }

    suspend fun getAsientos(idEvento: Int): List<Asiento> {
        val token = client.getToken() ?: SupabaseClient.SUPABASE_ANON_KEY
        val entradas = getEntradas(idEvento)
        if (entradas.isEmpty()) return emptyList()
        
        val idsEntradas = entradas.map { it.id }.joinToString(",")
        
        return client.client.get("${SupabaseClient.SUPABASE_URL}/rest/v1/asientos") {
            header("Authorization", "Bearer $token")
            header("apikey", SupabaseClient.SUPABASE_ANON_KEY)
            url {
                parameters.append("id_entrada", "in.($idsEntradas)")
                parameters.append("select", "*")
            }
        }.body()
    }

    suspend fun getCategorias(): List<Categoria> {
        val token = client.getToken() ?: SupabaseClient.SUPABASE_ANON_KEY
        return client.client.get("${SupabaseClient.SUPABASE_URL}/rest/v1/categorias") {
            header("Authorization", "Bearer $token")
            header("apikey", SupabaseClient.SUPABASE_ANON_KEY)
        }.body()
    }

    suspend fun getLugares(): List<Lugar> {
        val token = client.getToken() ?: SupabaseClient.SUPABASE_ANON_KEY
        return client.client.get("${SupabaseClient.SUPABASE_URL}/rest/v1/lugares") {
            header("Authorization", "Bearer $token")
            header("apikey", SupabaseClient.SUPABASE_ANON_KEY)
        }.body()
    }
}
