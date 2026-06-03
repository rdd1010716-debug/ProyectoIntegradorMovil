package com.chostito.app.data.repository

import com.chostito.app.data.remote.ApiProvider
import com.chostito.app.data.remote.dto.DashboardStatsDto
import com.chostito.app.data.remote.dto.EscanearQrRequest
import com.chostito.app.data.remote.dto.EscanearQrResponse
import com.chostito.app.data.remote.dto.VentaResumenDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardRepository @Inject constructor(
    private val apiProvider: ApiProvider
) {
    suspend fun getStats(): Result<DashboardStatsDto> {
        return try {
            val response = apiProvider.getApi().getDashboardStats()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMisVentas(): Result<List<VentaResumenDto>> {
        return try {
            val response = apiProvider.getApi().getMisVentas()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTodasGanancias(): Result<List<VentaResumenDto>> {
        return try {
            val response = apiProvider.getApi().getTodasGanancias()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun escanearQr(codigoQR: String): Result<EscanearQrResponse> {
        return try {
            val response = apiProvider.getApi().escanearEntrada(EscanearQrRequest(codigoQR))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Error al escanear"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
