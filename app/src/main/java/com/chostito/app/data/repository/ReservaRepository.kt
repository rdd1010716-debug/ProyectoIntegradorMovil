package com.chostito.app.data.repository

import com.chostito.app.data.remote.ApiProvider
import com.chostito.app.data.remote.dto.CrearReservaRequest
import com.chostito.app.data.remote.dto.EntradaReservaRequest
import com.chostito.app.data.remote.dto.ReservaDetalleDto
import com.chostito.app.data.remote.dto.ReservaDto
import com.chostito.app.domain.model.Reserva
import com.chostito.app.domain.model.toDomain
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReservaRepository @Inject constructor(
    private val apiProvider: ApiProvider
) {
    suspend fun crearReserva(eventoId: Int, entradas: List<Pair<String, Int>>): Result<Reserva> {
        return try {
            val request = CrearReservaRequest(
                eventoId = eventoId,
                entradas = entradas.map { EntradaReservaRequest(it.first, it.second) }
            )
            val response = apiProvider.getApi().createReserva(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Error al crear reserva"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMisReservas(): Result<List<Reserva>> {
        return try {
            val response = apiProvider.getApi().getMisReservas()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.map { it.toDomain() })
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getReservaDetalle(id: Int): Result<ReservaDetalleDto> {
        return try {
            val response = apiProvider.getApi().getReservaById(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cancelarReserva(id: Int): Result<Reserva> {
        return try {
            val response = apiProvider.getApi().cancelarReserva(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Error al cancelar"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
