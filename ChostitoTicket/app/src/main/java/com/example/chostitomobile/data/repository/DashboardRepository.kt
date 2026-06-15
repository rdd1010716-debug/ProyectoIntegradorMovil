package com.example.chostitomobile.data.repository

import com.example.chostitomobile.data.model.*
import com.example.chostitomobile.data.network.SupabaseClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class DashboardRepository(private val client: SupabaseClient) {

    suspend fun escanearQR(codigo: String): QRValidationResult {
        val token = client.getToken() ?: SupabaseClient.SUPABASE_ANON_KEY
        return client.client.post("${SupabaseClient.SUPABASE_URL}/rest/v1/rpc/escanear_qr") {
            header("Authorization", "Bearer $token")
            header("apikey", SupabaseClient.SUPABASE_ANON_KEY)
            setBody(mapOf("p_codigo" to codigo))
        }.body()
    }

    suspend fun getStats(): DashboardStats {
        val token = client.getToken() ?: SupabaseClient.SUPABASE_ANON_KEY
        return client.client.post("${SupabaseClient.SUPABASE_URL}/rest/v1/rpc/get_dashboard_stats") {
            header("Authorization", "Bearer $token")
            header("apikey", SupabaseClient.SUPABASE_ANON_KEY)
        }.body()
    }

    suspend fun getMisVentas(userId: String): List<Any> {
        val token = client.getToken() ?: SupabaseClient.SUPABASE_ANON_KEY
        return client.client.post("${SupabaseClient.SUPABASE_URL}/rest/v1/rpc/get_mis_ventas") {
            header("Authorization", "Bearer $token")
            header("apikey", SupabaseClient.SUPABASE_ANON_KEY)
            setBody(mapOf("p_id_organizador" to userId))
        }.body()
    }
}
