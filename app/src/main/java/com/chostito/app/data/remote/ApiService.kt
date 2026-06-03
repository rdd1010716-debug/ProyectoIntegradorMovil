package com.chostito.app.data.remote

import com.chostito.app.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Auth
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<LoginResponseDto>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponseDto>

    @POST("auth/solicitar-reset")
    suspend fun solicitarReset(@Body request: PasswordResetRequest): Response<Unit>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: PasswordResetConfirmRequest): Response<Unit>

    // Categorias
    @GET("categorias")
    suspend fun getCategorias(): Response<List<CategoriaDto>>

    // Eventos
    @GET("eventos")
    suspend fun getEventos(
        @Query("categoriaId") categoriaId: Int? = null,
        @Query("busqueda") busqueda: String? = null,
        @Query("estado") estado: String? = null
    ): Response<List<EventoDto>>

    @GET("eventos/{id}")
    suspend fun getEventoById(@Path("id") id: Int): Response<EventoDto>

    @GET("eventos/{id}/entradas")
    suspend fun getEntradasEvento(@Path("id") id: Int): Response<List<EntradaTipoDto>>

    @POST("eventos")
    suspend fun createEvento(@Body evento: EventoDto): Response<EventoDto>

    @GET("eventos/mis-eventos")
    suspend fun getMisEventos(): Response<List<EventoDto>>

    // Reservas
    @POST("reservas")
    suspend fun createReserva(@Body request: CrearReservaRequest): Response<ReservaDto>

    @GET("reservas/mis-reservas")
    suspend fun getMisReservas(): Response<List<ReservaDto>>

    @GET("reservas/{id}")
    suspend fun getReservaById(@Path("id") id: Int): Response<ReservaDetalleDto>

    @PUT("reservas/{id}/cancelar")
    suspend fun cancelarReserva(@Path("id") id: Int): Response<ReservaDto>

    // Pagos
    @GET("pagos/reserva/{reservaId}")
    suspend fun getPagoByReserva(@Path("reservaId") reservaId: Int): Response<PagoDto>

    @POST("pagos/{reservaId}/pagar")
    suspend fun pagarReserva(@Path("reservaId") reservaId: Int): Response<PagoDto>

    @POST("pagos/{reservaId}/qr")
    suspend fun generarQrPago(@Path("reservaId") reservaId: Int): Response<QrPayloadDto>

    // Favoritos
    @GET("favoritos")
    suspend fun getFavoritos(): Response<List<FavoritoDto>>

    @POST("favoritos/{eventoId}")
    suspend fun addFavorito(@Path("eventoId") eventoId: Int): Response<FavoritoDto>

    @DELETE("favoritos/{eventoId}")
    suspend fun removeFavorito(@Path("eventoId") eventoId: Int): Response<Unit>

    // Dashboard
    @GET("dashboard/stats")
    suspend fun getDashboardStats(): Response<DashboardStatsDto>

    @GET("dashboard/mis-ventas")
    suspend fun getMisVentas(): Response<List<VentaResumenDto>>

    @GET("dashboard/todas-ganancias")
    suspend fun getTodasGanancias(): Response<List<VentaResumenDto>>

    @POST("dashboard/entradas/escanear")
    suspend fun escanearEntrada(@Body request: EscanearQrRequest): Response<EscanearQrResponse>
}
