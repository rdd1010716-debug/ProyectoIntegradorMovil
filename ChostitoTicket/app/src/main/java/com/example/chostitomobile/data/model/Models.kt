package com.example.chostitomobile.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Perfil(
    val id: String,
    val email: String,
    val nombre: String,
    val telefono: String? = null,
    val rol: String,
    @SerialName("foto_url") val fotoUrl: String? = null,
    @SerialName("fecha_registro") val fechaRegistro: String? = null
)

@Serializable
data class Categoria(
    val id: Int? = null,
    val nombre: String,
    val descripcion: String? = null,
    val icono: String? = null
)

@Serializable
data class Lugar(
    val id: Int? = null,
    val nombre: String,
    val direccion: String? = null,
    val ciudad: String,
    val pais: String = "Bolivia"
)

@Serializable
data class Evento(
    val id: Int,
    @SerialName("id_organizador") val idOrganizador: String? = null,
    @SerialName("id_categoria") val idCategoria: Int? = null,
    @SerialName("id_lugar") val idLugar: Int? = null,
    val titulo: String,
    val eslogan: String? = null,
    val descripcion: String? = null,
    val fecha: String,
    val hora: String? = null,
    @SerialName("imagen_url") val imagenUrl: String? = null,
    val estado: String = "Publicado",
    @SerialName("precio_minimo") val precioMinimo: Double = 0.0,
    val categoria: String? = null,
    val lugar: String? = null,
    val ciudad: String? = null,
    val pais: String? = null,
    val categorias: Categoria? = null,
    val lugares: Lugar? = null
)

@Serializable
data class Entrada(
    val id: Int,
    @SerialName("id_evento") val idEvento: Int,
    val tipo: String,
    val precio: Double,
    @SerialName("cantidad_total") val cantidadTotal: Int = 0,
    @SerialName("cantidad_disponible") val cantidadDisponible: Int = 0
)

@Serializable
data class Asiento(
    val id: Int,
    @SerialName("id_entrada") val idEntrada: Int,
    val seccion: String = "General",
    val numero: String,
    val estado: String = "Activa"
)

@Serializable
data class Reserva(
    val id: Int,
    @SerialName("id_usuario") val idUsuario: String? = null,
    @SerialName("fecha_reserva") val fechaReserva: String? = null,
    val estado: String = "Pendiente",
    val total: Double = 0.0,
    @SerialName("cantidad_entradas") val cantidadEntradas: Int = 0,
    @SerialName("reserva_items") val items: List<ReservaItem> = emptyList(),
    val pagos: List<Pago> = emptyList()
)

@Serializable
data class ReservaItem(
    val id: Int,
    @SerialName("precio_unitario") val precioUnitario: Double = 0.0,
    val cantidad: Int = 1,
    val entradas: EntradaInfo? = null,
    val asientos: AsientoInfo? = null,
    @SerialName("entradas_vendidas") val entradasVendidas: List<EntradaVendida> = emptyList()
)

@Serializable
data class EntradaInfo(
    val tipo: String,
    @SerialName("id_evento") val idEvento: Int,
    val eventos: Evento? = null
)

@Serializable
data class AsientoInfo(
    val numero: String
)

@Serializable
data class EntradaVendida(
    @SerialName("codigo_qr") val codigoQr: String,
    val estado: String = "Activa"
)

@Serializable
data class Pago(
    val id: Int,
    @SerialName("id_reserva") val idReserva: Int,
    @SerialName("metodo_pago") val metodoPago: String = "QR",
    @SerialName("codigo_transaccion") val codigoTransaccion: String? = null,
    val monto: Double = 0.0,
    val estado: String = "Pendiente",
    @SerialName("fecha_pago") val fechaPago: String? = null
)

@Serializable
data class Favorito(
    val id: Int,
    @SerialName("eventos") val evento: Evento
)

@Serializable
data class FavoritoRequest(
    @SerialName("id_usuario") val idUsuario: String,
    @SerialName("id_evento") val idEvento: Int
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val access_token: String = "",
    val token_type: String = "",
    val expires_in: Int = 0,
    val refresh_token: String = "",
    val user: AuthUser? = null,
    val error: String? = null,
    @SerialName("error_description") val errorDescription: String? = null
)

@Serializable
data class AuthUser(
    val id: String
)

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val data: RegisterData
)

@Serializable
data class RegisterData(
    val nombre: String,
    val rol: String
)

@Serializable
data class ReservaRequest(
    @SerialName("p_id_usuario") val idUsuario: String,
    @SerialName("p_items") val items: List<ReservaItemPayload>,
    @SerialName("p_ids_entradas") val idsEntradas: List<Int>? = null
)

@Serializable
data class SimularPagoRequest(
    @SerialName("p_reserva_id") val reservaId: Int,
    @SerialName("p_metodo") val metodo: String = "QR"
)

@Serializable
data class GenerarQRRequest(
    @SerialName("p_reserva_id") val reservaId: Int
)

@Serializable
data class ReservaPayload(
    val items: List<ReservaItemPayload>,
    @SerialName("idsEntradas") val idsEntradas: List<Int>? = null
)

@Serializable
data class ReservaItemPayload(
    @SerialName("idEvento") val idEvento: Int,
    val tipo: String,
    val cantidad: Int,
    val precio: Double
)

@Serializable
data class QRValidationResult(
    val valido: Boolean,
    val mensaje: String? = null,
    val tipo: String? = null,
    val evento: String? = null,
    val comprador: String? = null,
    @SerialName("email_comprador") val emailComprador: String? = null,
    @SerialName("codigo_transaccion") val codigoTransaccion: String? = null,
    @SerialName("fecha_uso") val fechaUso: String? = null
)

@Serializable
data class DashboardStats(
    @SerialName("total_eventos") val totalEventos: Int = 0,
    @SerialName("entradas_vendidas") val entradasVendidas: Int = 0,
    @SerialName("total_usuarios") val totalUsuarios: Int = 0,
    @SerialName("total_recaudado") val totalRecaudado: Double = 0.0
)
