package com.example.chostitomobile.data.repository

import com.example.chostitomobile.data.model.*
import com.example.chostitomobile.data.network.SupabaseClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.get
import io.ktor.http.parameters

class ReservaRepository(private val client: SupabaseClient) {

    suspend fun crearReserva(items: List<ReservaItemPayload>, idsEntradas: List<Int>? = null): ReservaResult {
        val token = client.getToken() ?: SupabaseClient.SUPABASE_ANON_KEY
        val userId = client.getUserId() ?: return ReservaResult(error = "No autenticado")
        val response = client.client.post("${SupabaseClient.SUPABASE_URL}/rest/v1/rpc/crear_reserva") {
            header("Authorization", "Bearer $token")
            header("apikey", SupabaseClient.SUPABASE_ANON_KEY)
            setBody(ReservaRequest(
                idUsuario = userId,
                items = items,
                idsEntradas = idsEntradas
            ))
        }
        return response.body()
    }

    suspend fun misReservas(): List<Reserva> {
        val token = client.getToken() ?: SupabaseClient.SUPABASE_ANON_KEY
        val userId = client.getUserId() ?: return emptyList()
        return client.client.get("${SupabaseClient.SUPABASE_URL}/rest/v1/reservas") {
            header("Authorization", "Bearer $token")
            header("apikey", SupabaseClient.SUPABASE_ANON_KEY)
            url {
                parameters.append("id_usuario", "eq.$userId")
                parameters.append("select", "*,reserva_items(*,entradas(tipo,id_evento),asientos(numero),entradas_vendidas(codigo_qr,estado)),pagos(*)")
                parameters.append("order", "fecha_reserva.desc")
            }
        }.body()
    }

    suspend fun cancelarReserva(id: Int) {
        val token = client.getToken() ?: SupabaseClient.SUPABASE_ANON_KEY
        client.client.post("${SupabaseClient.SUPABASE_URL}/rest/v1/reservas") {
            header("Authorization", "Bearer $token")
            header("apikey", SupabaseClient.SUPABASE_ANON_KEY)
            url { parameters.append("id", "eq.$id") }
            setBody(mapOf("estado" to "Cancelada"))
        }
    }

    suspend fun simularPago(reservaId: Int): PagoResult {
        val token = client.getToken() ?: SupabaseClient.SUPABASE_ANON_KEY
        val response = client.client.post("${SupabaseClient.SUPABASE_URL}/rest/v1/rpc/simular_pago") {
            header("Authorization", "Bearer $token")
            header("apikey", SupabaseClient.SUPABASE_ANON_KEY)
            setBody(SimularPagoRequest(reservaId = reservaId, metodo = "QR"))
        }
        return response.body()
    }

    suspend fun generarQR(reservaId: Int): QRResult {
        val token = client.getToken() ?: SupabaseClient.SUPABASE_ANON_KEY
        val response = client.client.post("${SupabaseClient.SUPABASE_URL}/rest/v1/rpc/generar_qr_pago") {
            header("Authorization", "Bearer $token")
            header("apikey", SupabaseClient.SUPABASE_ANON_KEY)
            setBody(GenerarQRRequest(reservaId = reservaId))
        }
        return response.body()
    }
}

@kotlinx.serialization.Serializable
data class ReservaResult(
    val id: Int? = null,
    val total: Double? = null,
    @kotlinx.serialization.SerialName("cantidad_entradas") val cantidadEntradas: Int? = null,
    val error: String? = null
)

@kotlinx.serialization.Serializable
data class PagoResult(
    val success: Boolean = false,
    @kotlinx.serialization.SerialName("codigo_transaccion") val codigoTransaccion: String? = null,
    val monto: Double? = null,
    val error: String? = null
)

@kotlinx.serialization.Serializable
data class QRResult(
    @kotlinx.serialization.SerialName("qr_data") val qrData: String? = null,
    @kotlinx.serialization.SerialName("codigo_transaccion") val codigoTransaccion: String? = null,
    val monto: Double? = null,
    @kotlinx.serialization.SerialName("reserva_id") val reservaId: Int? = null,
    val error: String? = null
)
