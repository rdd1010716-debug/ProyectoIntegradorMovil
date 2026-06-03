package com.chostito.app.data.repository

import com.chostito.app.data.remote.ApiProvider
import com.chostito.app.data.remote.dto.PagoDto
import com.chostito.app.data.remote.dto.QrPayloadDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PagoRepository @Inject constructor(
    private val apiProvider: ApiProvider
) {
    suspend fun pagarReserva(reservaId: Int): Result<PagoDto> {
        return try {
            val response = apiProvider.getApi().pagarReserva(reservaId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Error de pago"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generarQr(reservaId: Int): Result<QrPayloadDto> {
        return try {
            val response = apiProvider.getApi().generarQrPago(reservaId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Error QR"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPagoByReserva(reservaId: Int): Result<PagoDto> {
        return try {
            val response = apiProvider.getApi().getPagoByReserva(reservaId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
