package com.example.chostitomobile.data.repository

import com.example.chostitomobile.data.model.DashboardStats
import com.example.chostitomobile.data.network.SupabaseClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post

class AdminRepository(private val client: SupabaseClient) {

    suspend fun getStats(): DashboardStats {
        val token = client.getToken() ?: SupabaseClient.SUPABASE_ANON_KEY
        val response: List<DashboardStats> = client.client.post("${SupabaseClient.SUPABASE_URL}/rest/v1/rpc/get_dashboard_stats") {
            header("Authorization", "Bearer $token")
            header("apikey", SupabaseClient.SUPABASE_ANON_KEY)
        }.body()
        return response.firstOrNull() ?: DashboardStats()
    }
}
